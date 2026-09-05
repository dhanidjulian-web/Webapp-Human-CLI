package com.agon.app.sandbox

import com.agon.app.data.model.ProcessLine
import com.agon.app.data.model.ProcessResult
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for process execution plane.
 * Implementations: local ProcessBuilder, remote SSH, proot, microbox, etc.
 */
interface ProcessExecutor {

    /**
     * Execute a command with resource limits.
     *
     * @param command argv-style command list OR single shell command string (implementation-defined)
     * @param workingDirectory must be inside the allowed sandbox root
     * @param environment extra env vars (merged with a sanitized base)
     * @param timeoutMs hard wall-clock timeout; process is destroyed if exceeded
     * @param shell when true, run via `/system/bin/sh -c`
     */
    suspend fun execute(
        command: String,
        workingDirectory: String,
        environment: Map<String, String> = emptyMap(),
        timeoutMs: Long = LocalProcessExecutor.DEFAULT_TIMEOUT_MS,
        shell: Boolean = true,
    ): ProcessResult

    /**
     * Stream stdout/stderr lines while a command runs.
     * Completes when the process exits or is killed.
     */
    fun executeStreaming(
        command: String,
        workingDirectory: String,
        environment: Map<String, String> = emptyMap(),
        timeoutMs: Long = LocalProcessExecutor.DEFAULT_TIMEOUT_MS,
        shell: Boolean = true,
    ): Flow<ProcessLine>

    /** Force-kill any still-running process tracked by this executor for [executionId]. */
    fun cancel(executionId: String): Boolean

    fun isPathAllowed(path: String): Boolean
}
