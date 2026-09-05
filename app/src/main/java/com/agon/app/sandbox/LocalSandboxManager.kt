package com.agon.app.sandbox

import android.content.Context
import android.util.Log
import com.agon.app.data.db.MusGoDatabase
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.db.entity.TerminalSessionEntity
import com.agon.app.data.model.CommandClassification
import com.agon.app.data.model.CommandPolicyMatch
import com.agon.app.data.model.CommandStatus
import com.agon.app.data.model.LinuxDistro
import com.agon.app.data.model.ProcessResult
import com.agon.app.data.model.SandboxInfo
import com.agon.app.data.model.SandboxStatus
import com.agon.app.data.model.ShellType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * Real local sandbox controller.
 * Storage layout:
 *   cache/sandbox/<sandboxId>/
 *     work/          — cwd for sessions
 *     exports/       — preserved on destroy
 *     .musgo_meta    — marker
 *   cache/sandbox/_logs/ — command stdout/stderr (retained)
 *   cache/sandbox/_exports_archive/<sandboxId>/ — moved exports on destroy
 */
class LocalSandboxManager(
    context: Context,
    private val db: MusGoDatabase,
    private val executor: LocalProcessExecutor,
) : SandboxManager {

    companion object {
        private const val TAG = "LocalSandboxManager"
        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    }

    private val appContext = context.applicationContext
    private val mutex = Mutex()
    private val policyCache = ConcurrentHashMap<String, Pattern>()

    override fun observeSandboxes(): Flow<List<SandboxEntity>> = db.sandboxDao().observeAll()

    override fun observeSandbox(id: Long): Flow<SandboxEntity?> = db.sandboxDao().observeById(id)

    override suspend fun create(name: String, distro: LinuxDistro): SandboxEntity =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                require(name.isNotBlank()) { "Sandbox name required" }
                val trimmed = name.trim()
                // Placeholder path updated after insert with real id
                val tempPath = File(executor.rootPath, "_pending").absolutePath
                val id = db.sandboxDao().insert(
                    SandboxEntity(
                        name = trimmed,
                        linuxDistro = distro,
                        storagePath = tempPath,
                        status = SandboxStatus.CREATED,
                    ),
                )
                val root = File(executor.rootPath, "sb_$id").apply { mkdirs() }
                File(root, "work").mkdirs()
                File(root, "exports").mkdirs()
                File(root, "tmp").mkdirs()
                File(root, ".musgo_meta").writeText(
                    buildString {
                        appendLine("id=$id")
                        appendLine("name=$trimmed")
                        appendLine("distro=${distro.name}")
                        appendLine("created=${System.currentTimeMillis()}")
                        appendLine("backend=local-process")
                    },
                )
                val entity = SandboxEntity(
                    id = id,
                    name = trimmed,
                    linuxDistro = distro,
                    storagePath = root.absolutePath,
                    status = SandboxStatus.CREATED,
                )
                db.sandboxDao().update(entity)
                Log.i(TAG, "Created sandbox id=$id path=${root.absolutePath}")
                entity
            }
        }

    override suspend fun initialize(sandboxId: Long): SandboxEntity =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val entity = db.sandboxDao().getById(sandboxId)
                    ?: throw IllegalArgumentException("Sandbox $sandboxId not found")
                try {
                    val root = File(entity.storagePath)
                    if (!root.exists()) root.mkdirs()
                    File(root, "work").mkdirs()
                    File(root, "exports").mkdirs()
                    File(root, "tmp").mkdirs()

                    // Distro marker — documents intended userspace flavor for future proot roots
                    File(root, "DISTRO").writeText(entity.linuxDistro.name)

                    // Smoke-test the local shell
                    val smoke = executor.execute(
                        command = "echo MUSGO_INIT_OK && pwd && uname -a 2>/dev/null || echo android-userspace",
                        workingDirectory = File(root, "work").absolutePath,
                        timeoutMs = 10_000L,
                    )
                    if (smoke.exitCode != 0 && !smoke.stdout.contains("MUSGO_INIT_OK")) {
                        val err = "Init smoke test failed: exit=${smoke.exitCode} ${smoke.stderr.take(200)}"
                        db.sandboxDao().updateStatus(sandboxId, SandboxStatus.ERROR, err)
                        throw IllegalStateException(err)
                    }

                    File(root, ".musgo_init").writeText(
                        "initialized=${System.currentTimeMillis()}\nstdout=${smoke.stdout.take(500)}\n",
                    )

                    db.sandboxDao().updateStatus(sandboxId, SandboxStatus.RUNNING, null)
                    db.sandboxDao().getById(sandboxId)!!
                } catch (e: Exception) {
                    db.sandboxDao().updateStatus(
                        sandboxId,
                        SandboxStatus.ERROR,
                        e.message?.take(300),
                    )
                    throw e
                }
            }
        }

    override suspend fun openSession(sandboxId: Long, shellType: ShellType): Long =
        withContext(Dispatchers.IO) {
            val sandbox = db.sandboxDao().getById(sandboxId)
                ?: throw IllegalArgumentException("Sandbox $sandboxId not found")
            require(sandbox.status == SandboxStatus.RUNNING || sandbox.status == SandboxStatus.CREATED) {
                "Sandbox not runnable: ${sandbox.status}"
            }
            if (sandbox.status == SandboxStatus.CREATED) {
                initialize(sandboxId)
            }
            val work = File(sandbox.storagePath, "work").apply { mkdirs() }
            val envJson = json.encodeToString(
                mapOf(
                    "MUSGO_SANDBOX_ID" to sandboxId.toString(),
                    "MUSGO_DISTRO" to sandbox.linuxDistro.name,
                ),
            )
            val sessionId = db.terminalSessionDao().insert(
                TerminalSessionEntity(
                    sandboxId = sandboxId,
                    shellType = shellType,
                    currentWorkingDirectory = work.absolutePath,
                    envVariables = envJson,
                    isActive = true,
                ),
            )
            val active = db.terminalSessionDao().countActiveBySandbox(sandboxId)
            db.sandboxDao().updateSessionCount(sandboxId, active)
            if (sandbox.status != SandboxStatus.RUNNING) {
                db.sandboxDao().updateStatus(sandboxId, SandboxStatus.RUNNING, null)
            }
            sessionId
        }

    override suspend fun executeCommand(
        sessionId: Long,
        command: String,
        timeoutMs: Long,
    ): ProcessResult = withContext(Dispatchers.IO) {
        require(command.isNotBlank()) { "Empty command" }

        val session = db.terminalSessionDao().getById(sessionId)
            ?: throw IllegalArgumentException("Session $sessionId not found")
        require(session.isActive) { "Session is inactive" }

        val sandbox = db.sandboxDao().getById(session.sandboxId)
            ?: throw IllegalArgumentException("Sandbox missing for session")
        require(sandbox.status == SandboxStatus.RUNNING) {
            "Sandbox not RUNNING (${sandbox.status})"
        }

        val policy = evaluatePolicy(command)
        if (policy?.classification == CommandClassification.BLOCKED) {
            val blocked = db.commandExecutionDao().insert(
                CommandExecutionEntity(
                    sessionId = sessionId,
                    commandText = command,
                    status = CommandStatus.FAILED,
                    exitCode = 126,
                    durationMs = 0,
                    classification = policy.classification.name,
                    stdoutPath = null,
                    stderrPath = null,
                ),
            )
            // Write a synthetic stderr note
            val errFile = File(executor.rootPath, "_logs/blocked_$blocked.stderr.log")
            errFile.parentFile?.mkdirs()
            errFile.writeText("BLOCKED by policy #${policy.policyId}: ${policy.description}\n")
            db.commandExecutionDao().complete(
                id = blocked,
                status = CommandStatus.FAILED,
                exitCode = 126,
                durationMs = 0,
                stdoutPath = null,
                stderrPath = errFile.absolutePath,
                timedOut = false,
            )
            return@withContext ProcessResult(
                exitCode = 126,
                stdout = "",
                stderr = "BLOCKED by policy: ${policy.description} (${policy.pattern})",
                durationMs = 0,
                stdoutPath = null,
                stderrPath = errFile.absolutePath,
            )
        }

        val execId = db.commandExecutionDao().insert(
            CommandExecutionEntity(
                sessionId = sessionId,
                commandText = command,
                status = CommandStatus.QUEUED,
                classification = policy?.classification?.name,
            ),
        )
        db.commandExecutionDao().updateStatus(execId, CommandStatus.RUNNING)

        val env = parseEnv(session.envVariables)
        val result = try {
            executor.execute(
                command = command,
                workingDirectory = session.currentWorkingDirectory,
                environment = env,
                timeoutMs = timeoutMs,
                shell = true,
            )
        } catch (e: Exception) {
            db.commandExecutionDao().complete(
                id = execId,
                status = CommandStatus.FAILED,
                exitCode = -1,
                durationMs = 0,
                stdoutPath = null,
                stderrPath = null,
                timedOut = false,
            )
            throw e
        }

        val status = when {
            result.timedOut -> CommandStatus.FAILED
            result.exitCode == 0 -> CommandStatus.SUCCESS
            else -> CommandStatus.FAILED
        }
        db.commandExecutionDao().complete(
            id = execId,
            status = status,
            exitCode = result.exitCode,
            durationMs = result.durationMs,
            stdoutPath = result.stdoutPath,
            stderrPath = result.stderrPath,
            timedOut = result.timedOut,
        )

        // Track simple `cd` for session cwd (best-effort, local only)
        trackCd(session, command, result)

        result
    }

    override suspend fun stop(sandboxId: Long): SandboxEntity =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val sandbox = db.sandboxDao().getById(sandboxId)
                    ?: throw IllegalArgumentException("Sandbox $sandboxId not found")
                executor.cancelAll()
                stopActiveSessions(sandboxId)
                db.sandboxDao().updateSessionCount(sandboxId, 0)
                db.sandboxDao().updateStatus(sandboxId, SandboxStatus.STOPPED, null)
                db.sandboxDao().getById(sandboxId) ?: sandbox.copy(status = SandboxStatus.STOPPED)
            }
        }

    override suspend fun destroy(sandboxId: Long, deleteDatabaseRow: Boolean) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val sandbox = db.sandboxDao().getById(sandboxId)
                if (sandbox == null) return@withLock

                executor.cancelAll()
                stopActiveSessions(sandboxId)

                val root = File(sandbox.storagePath)
                // Preserve exports — move to archive outside live work tree
                val exports = File(root, "exports")
                if (exports.exists() && exports.isDirectory) {
                    val archiveRoot = File(executor.rootPath, "_exports_archive").apply { mkdirs() }
                    val dest = File(archiveRoot, "sb_$sandboxId")
                    if (dest.exists()) dest.deleteRecursively()
                    exports.copyRecursively(dest, overwrite = true)
                    Log.i(TAG, "Preserved exports → ${dest.absolutePath}")
                }

                // Remove live workspace only — do NOT touch _logs (command history files)
                if (root.exists() && root.absolutePath.startsWith(executor.rootPath)) {
                    root.deleteRecursively()
                }

                // Soft-destroy: keep DB audit trail + command history; archive exports.
                if (deleteDatabaseRow) {
                    db.sandboxDao().update(
                        sandbox.copy(
                            status = SandboxStatus.STOPPED,
                            storagePath = File(executor.rootPath, "_exports_archive/sb_$sandboxId").absolutePath,
                            activeSessionCount = 0,
                            lastError = "Destroyed; exports archived if any",
                            updatedAt = System.currentTimeMillis(),
                        ),
                    )
                    stopActiveSessions(sandboxId)
                }

                Log.i(TAG, "Destroyed sandbox runtime id=$sandboxId")
                Unit
            }
        }
    }

    override suspend fun getInfo(sandboxId: Long): SandboxInfo? =
        withContext(Dispatchers.IO) {
            val s = db.sandboxDao().getById(sandboxId) ?: return@withContext null
            SandboxInfo(
                id = s.id,
                name = s.name,
                distro = s.linuxDistro,
                storagePath = s.storagePath,
                status = s.status,
                activeSessionCount = s.activeSessionCount,
                workDirExists = File(s.storagePath, "work").exists() || File(s.storagePath).exists(),
            )
        }

    override fun evaluatePolicy(command: String): CommandPolicyMatch? {
        // Blocking call used from IO context; policies loaded sync from memory cache after first hit
        // For first-call correctness we use a runBlocking-free approach: patterns compiled from last known
        // The repository seeds DB; we read via runBlocking is bad — use synchronized refresh.
        return try {
            evaluatePolicyBlocking(command)
        } catch (_: Exception) {
            null
        }
    }

    private var cachedPolicies: List<com.agon.app.data.db.entity.CommandPolicyEntity> = emptyList()
    private var policiesLoadedAt = 0L

    private fun evaluatePolicyBlocking(command: String): CommandPolicyMatch? {
        val now = System.currentTimeMillis()
        if (now - policiesLoadedAt > 5_000 || cachedPolicies.isEmpty()) {
            // Best-effort: use default patterns if DB not ready on cold path
            try {
                // This may be called from background; Room allows other-thread queries
                val list = kotlinx.coroutines.runBlocking {
                    db.commandPolicyDao().getEnabled()
                }
                if (list.isNotEmpty()) {
                    cachedPolicies = list
                    policiesLoadedAt = now
                } else if (cachedPolicies.isEmpty()) {
                    cachedPolicies = MusGoDatabase.defaultPolicies()
                    policiesLoadedAt = now
                }
            } catch (_: Exception) {
                if (cachedPolicies.isEmpty()) {
                    cachedPolicies = MusGoDatabase.defaultPolicies()
                    policiesLoadedAt = now
                }
            }
        }

        // Priority: BLOCKED > DANGEROUS > REVIEW_REQUIRED > SAFE
        val order = listOf(
            CommandClassification.BLOCKED,
            CommandClassification.DANGEROUS,
            CommandClassification.REVIEW_REQUIRED,
            CommandClassification.SAFE,
        )
        val sorted = cachedPolicies.sortedBy { order.indexOf(it.classification).let { i -> if (i < 0) 99 else i } }

        for (policy in sorted) {
            val pattern = policyCache.getOrPut(policy.commandPattern) {
                try {
                    Pattern.compile(policy.commandPattern, Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
                } catch (_: Exception) {
                    Pattern.compile(Pattern.quote(policy.commandPattern))
                }
            }
            if (pattern.matcher(command).find()) {
                return CommandPolicyMatch(
                    policyId = policy.id,
                    pattern = policy.commandPattern,
                    classification = policy.classification,
                    description = policy.description,
                )
            }
        }
        return null
    }

    private suspend fun stopActiveSessions(sandboxId: Long) {
        // Observe one emission via get — use a query through count and deactivate
        // Implement with a small loop using Room @Query we already have: get sessions via
        // inserting a helper — use raw open helper as fallback
        val cursor = db.openHelper.readableDatabase.query(
            "SELECT id FROM terminal_sessions WHERE sandboxId = ? AND isActive = 1",
            arrayOf(sandboxId.toString()),
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                db.terminalSessionDao().deactivate(id)
            }
        }
    }

    private fun parseEnv(envJson: String): Map<String, String> {
        return try {
            json.decodeFromString<Map<String, String>>(envJson)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private suspend fun trackCd(session: TerminalSessionEntity, command: String, result: ProcessResult) {
        val trimmed = command.trim()
        if (!trimmed.startsWith("cd") || result.exitCode != 0) return
        val parts = trimmed.split(Regex("\\s+"), limit = 2)
        if (parts.size < 2) return
        val target = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
        val base = File(session.currentWorkingDirectory)
        val next = when {
            target == "~" || target.isEmpty() -> File(session.currentWorkingDirectory).parentFile ?: base
            File(target).isAbsolute -> File(target)
            else -> File(base, target)
        }
        try {
            val canonical = next.canonicalFile
            if (executor.isPathAllowed(canonical.absolutePath) && canonical.isDirectory) {
                db.terminalSessionDao().updateCwd(session.id, canonical.absolutePath)
            }
        } catch (_: Exception) {
            // ignore invalid cd
        }
    }
}
