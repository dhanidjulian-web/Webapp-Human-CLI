package com.agon.app.sandbox

import android.content.Context
import android.os.Build
import android.util.Log
import com.agon.app.data.model.OutputStreamType
import com.agon.app.data.model.ProcessLine
import com.agon.app.data.model.ProcessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Production local process executor using Android [ProcessBuilder].
 *
 * Security constraints:
 * - Working directory MUST resolve under app-private sandbox root:
 *   `/data/data/<package>/cache/sandbox/` (or filesDir equivalent)
 * - CPU wall-clock timeout default 30s → [Process.destroy]
 * - stdout/stderr captured line-by-line async and persisted to private log files
 */
class LocalProcessExecutor(
    context: Context,
) : ProcessExecutor {

    companion object {
        const val DEFAULT_TIMEOUT_MS = 30_000L
        private const val TAG = "LocalProcessExecutor"
        private val FORBIDDEN_ENV = setOf(
            "LD_PRELOAD",
            "LD_LIBRARY_PATH",
            "DYLD_INSERT_LIBRARIES",
        )
    }

    private val appContext = context.applicationContext
    private val sandboxRoot: File = File(appContext.cacheDir, "sandbox").apply { mkdirs() }
    private val logsRoot: File = File(sandboxRoot, "_logs").apply { mkdirs() }
    private val activeProcesses = ConcurrentHashMap<String, Process>()

    val rootPath: String get() = sandboxRoot.absolutePath

    override fun isPathAllowed(path: String): Boolean {
        return try {
            val target = File(path).canonicalFile
            val root = sandboxRoot.canonicalFile
            target.path == root.path || target.path.startsWith(root.path + File.separator)
        } catch (_: Exception) {
            false
        }
    }

    fun ensureSandboxDir(relativeOrAbsolute: String): File {
        val dir = if (File(relativeOrAbsolute).isAbsolute) {
            File(relativeOrAbsolute)
        } else {
            File(sandboxRoot, relativeOrAbsolute)
        }
        require(isPathAllowed(dir.absolutePath) || !dir.exists()) {
            // Allow creation under root
            "Path escapes sandbox root: ${dir.absolutePath}"
        }
        val canonicalParent = dir.parentFile?.canonicalFile
        val root = sandboxRoot.canonicalFile
        require(
            dir.absolutePath.startsWith(root.path) ||
                (canonicalParent != null && (canonicalParent.path == root.path || canonicalParent.path.startsWith(root.path + File.separator))),
        ) {
            "Path escapes sandbox root: ${dir.absolutePath}"
        }
        if (!dir.exists()) dir.mkdirs()
        val canonical = dir.canonicalFile
        require(canonical.path == root.path || canonical.path.startsWith(root.path + File.separator)) {
            "Canonical path escapes sandbox: ${canonical.path}"
        }
        return canonical
    }

    override suspend fun execute(
        command: String,
        workingDirectory: String,
        environment: Map<String, String>,
        timeoutMs: Long,
        shell: Boolean,
    ): ProcessResult = withContext(Dispatchers.IO) {
        require(command.isNotBlank()) { "Command must not be blank" }
        val workDir = resolveWorkDir(workingDirectory)
        val executionId = UUID.randomUUID().toString()
        val stdoutFile = File(logsRoot, "${executionId}.stdout.log")
        val stderrFile = File(logsRoot, "${executionId}.stderr.log")

        val argv = buildArgv(command, shell)
        val pb = ProcessBuilder(argv)
            .directory(workDir)
            .redirectErrorStream(false)

        sanitizeEnvironment(pb.environment(), environment)

        val startedAt = System.currentTimeMillis()
        var process: Process? = null
        var timedOut = false
        var killed = false

        try {
            process = pb.start()
            activeProcesses[executionId] = process

            val stdoutBuilder = StringBuilder()
            val stderrBuilder = StringBuilder()

            coroutineScope {
                val outJob = async {
                    drainStream(process.inputStream, stdoutFile, stdoutBuilder)
                }
                val errJob = async {
                    drainStream(process.errorStream, stderrFile, stderrBuilder)
                }

                val finished = withTimeoutOrNull(timeoutMs) {
                    while (isActive) {
                        try {
                            process.exitValue()
                            break
                        } catch (_: IllegalThreadStateException) {
                            // still running
                            kotlinx.coroutines.delay(50)
                        }
                    }
                    true
                }

                if (finished == null) {
                    timedOut = true
                    killed = true
                    Log.w(TAG, "Timeout ${timeoutMs}ms — destroying process for: ${command.take(80)}")
                    destroyProcess(process)
                }

                // Give streams a moment to flush after kill
                runCatching { outJob.await() }
                runCatching { errJob.await() }
            }

            val exitCode = try {
                val finished = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    process.waitFor(2, TimeUnit.SECONDS)
                } else {
                    process.waitFor()
                    true
                }
                if (!finished) {
                    destroyProcess(process)
                    killed = true
                    -9
                } else {
                    process.exitValue()
                }
            } catch (_: Exception) {
                -1
            }

            val duration = System.currentTimeMillis() - startedAt
            ProcessResult(
                exitCode = if (timedOut) 124 else exitCode,
                stdout = stdoutBuilder.toString(),
                stderr = stderrBuilder.toString() + if (timedOut) {
                    "\n[MusGo-OS] Process killed after ${timeoutMs}ms timeout"
                } else {
                    ""
                },
                durationMs = duration,
                stdoutPath = stdoutFile.absolutePath,
                stderrPath = stderrFile.absolutePath,
                timedOut = timedOut,
                killed = killed,
            )
        } catch (e: CancellationException) {
            process?.let { destroyProcess(it); killed = true }
            throw e
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startedAt
            runCatching {
                stderrFile.appendText("\n[MusGo-OS] Executor error: ${e.message}\n")
            }
            ProcessResult(
                exitCode = -1,
                stdout = "",
                stderr = "Executor error: ${e.javaClass.simpleName}: ${e.message}",
                durationMs = duration,
                stdoutPath = stdoutFile.takeIf { it.exists() }?.absolutePath,
                stderrPath = stderrFile.takeIf { it.exists() }?.absolutePath,
                timedOut = false,
                killed = killed,
            )
        } finally {
            activeProcesses.remove(executionId)
            process?.let {
                runCatching { it.inputStream.close() }
                runCatching { it.errorStream.close() }
                runCatching { it.destroy() }
            }
        }
    }

    override fun executeStreaming(
        command: String,
        workingDirectory: String,
        environment: Map<String, String>,
        timeoutMs: Long,
        shell: Boolean,
    ): Flow<ProcessLine> = callbackFlow {
        val workDir = resolveWorkDir(workingDirectory)
        val executionId = UUID.randomUUID().toString()
        val argv = buildArgv(command, shell)
        val pb = ProcessBuilder(argv).directory(workDir).redirectErrorStream(false)
        sanitizeEnvironment(pb.environment(), environment)

        val process = pb.start()
        activeProcesses[executionId] = process
        val closed = AtomicBoolean(false)

        val outJob = launch(Dispatchers.IO) {
            readLines(process.inputStream) { line ->
                trySend(ProcessLine(OutputStreamType.STDOUT, line))
            }
        }
        val errJob = launch(Dispatchers.IO) {
            readLines(process.errorStream) { line ->
                trySend(ProcessLine(OutputStreamType.STDERR, line))
            }
        }
        val watchdog = launch(Dispatchers.IO) {
            val ok = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
            } else {
                val deadline = System.currentTimeMillis() + timeoutMs
                var finished = false
                while (System.currentTimeMillis() < deadline) {
                    try {
                        process.exitValue()
                        finished = true
                        break
                    } catch (_: IllegalThreadStateException) {
                        Thread.sleep(50)
                    }
                }
                finished
            }
            if (!ok) {
                destroyProcess(process)
                trySend(
                    ProcessLine(
                        OutputStreamType.STDERR,
                        "[MusGo-OS] Process killed after ${timeoutMs}ms timeout",
                    ),
                )
            }
            outJob.join()
            errJob.join()
            if (closed.compareAndSet(false, true)) {
                close()
            }
        }

        awaitClose {
            closed.set(true)
            outJob.cancel()
            errJob.cancel()
            watchdog.cancel()
            destroyProcess(process)
            activeProcesses.remove(executionId)
        }
    }.flowOn(Dispatchers.IO)

    override fun cancel(executionId: String): Boolean {
        val p = activeProcesses.remove(executionId) ?: return false
        destroyProcess(p)
        return true
    }

    fun cancelAll() {
        activeProcesses.keys.toList().forEach { cancel(it) }
    }

    private fun resolveWorkDir(workingDirectory: String): File {
        val dir = if (workingDirectory.isBlank()) {
            sandboxRoot
        } else if (File(workingDirectory).isAbsolute) {
            File(workingDirectory)
        } else {
            File(sandboxRoot, workingDirectory)
        }
        if (!dir.exists()) dir.mkdirs()
        val canonical = dir.canonicalFile
        require(isPathAllowed(canonical.absolutePath)) {
            "Working directory outside sandbox root: ${canonical.absolutePath} (root=${sandboxRoot.canonicalPath})"
        }
        return canonical
    }

    private fun buildArgv(command: String, shell: Boolean): List<String> {
        if (!shell) {
            return command.trim().split(Regex("\\s+"))
        }
        // Prefer toybox/sh available on Android
        val sh = listOf("/system/bin/sh", "/system/xbin/sh", "/bin/sh")
            .firstOrNull { File(it).canExecute() }
            ?: "sh"
        return listOf(sh, "-c", command)
    }

    private fun sanitizeEnvironment(
        env: MutableMap<String, String>,
        extra: Map<String, String>,
    ) {
        FORBIDDEN_ENV.forEach { env.remove(it) }
        // Keep a minimal PATH for Android toolbox/toybox
        if (!env.containsKey("PATH") || env["PATH"].isNullOrBlank()) {
            env["PATH"] = "/system/bin:/system/xbin:/vendor/bin"
        }
        env["HOME"] = sandboxRoot.absolutePath
        env["TMPDIR"] = File(sandboxRoot, "tmp").apply { mkdirs() }.absolutePath
        env["MUSGO_SANDBOX"] = sandboxRoot.absolutePath
        for ((k, v) in extra) {
            if (k.uppercase() in FORBIDDEN_ENV) continue
            if (k.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))) {
                env[k] = v
            }
        }
    }

    private fun drainStream(stream: InputStream, outFile: File, builder: StringBuilder) {
        FileOutputStream(outFile, true).use { fos ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val l = line ?: break
                    builder.append(l).append('\n')
                    fos.write((l + "\n").toByteArray(Charsets.UTF_8))
                    fos.flush()
                }
            }
        }
    }

    private fun readLines(stream: InputStream, onLine: (String) -> Unit) {
        BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                onLine(line ?: break)
            }
        }
    }

    private fun destroyProcess(process: Process) {
        try {
            process.destroy()
            val died = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.waitFor(500, TimeUnit.MILLISECONDS)
            } else {
                var done = false
                val deadline = System.currentTimeMillis() + 500
                while (System.currentTimeMillis() < deadline) {
                    try {
                        process.exitValue()
                        done = true
                        break
                    } catch (_: IllegalThreadStateException) {
                        Thread.sleep(40)
                    }
                }
                done
            }
            if (!died) {
                forceDestroy(process)
            }
        } catch (e: Exception) {
            Log.w(TAG, "destroyProcess: ${e.message}")
            forceDestroy(process)
        }
    }

    private fun forceDestroy(process: Process) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching { process.destroyForcibly() }
        } else {
            runCatching { process.destroy() }
        }
    }
}
