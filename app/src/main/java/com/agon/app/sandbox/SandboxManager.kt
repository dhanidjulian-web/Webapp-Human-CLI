package com.agon.app.sandbox

import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.model.LinuxDistro
import com.agon.app.data.model.ProcessResult
import com.agon.app.data.model.SandboxInfo
import com.agon.app.data.model.ShellType
import kotlinx.coroutines.flow.Flow

/**
 * Control-plane abstraction for sandbox lifecycle.
 * Extensible toward remote SSH / proot / microbox backends.
 */
interface SandboxManager {

    fun observeSandboxes(): Flow<List<SandboxEntity>>

    fun observeSandbox(id: Long): Flow<SandboxEntity?>

    /** Create sandbox record + private storage directory. */
    suspend fun create(
        name: String,
        distro: LinuxDistro = LinuxDistro.ALPINE,
    ): SandboxEntity

    /** Initialize runtime (dirs, marker files, baseline env). */
    suspend fun initialize(sandboxId: Long): SandboxEntity

    /** Open a terminal session bound to the sandbox. */
    suspend fun openSession(
        sandboxId: Long,
        shellType: ShellType = ShellType.SH,
    ): Long

    /**
     * Execute a command inside the sandbox via the process executor.
     * Persists a CommandExecutionEntity row with real exit codes and log paths.
     */
    suspend fun executeCommand(
        sessionId: Long,
        command: String,
        timeoutMs: Long = LocalProcessExecutor.DEFAULT_TIMEOUT_MS,
    ): ProcessResult

    /** Soft-stop: mark STOPPED, deactivate sessions, cancel running processes. */
    suspend fun stop(sandboxId: Long): SandboxEntity

    /**
     * Destroy sandbox runtime workspace.
     * Does NOT delete CommandExecution history or exported artifacts outside the live work tree.
     * Database sandbox row is removed; execution logs under _logs/exports are retained when possible.
     */
    suspend fun destroy(sandboxId: Long, deleteDatabaseRow: Boolean = true)

    suspend fun getInfo(sandboxId: Long): SandboxInfo?

    fun evaluatePolicy(command: String): com.agon.app.data.model.CommandPolicyMatch?
}
