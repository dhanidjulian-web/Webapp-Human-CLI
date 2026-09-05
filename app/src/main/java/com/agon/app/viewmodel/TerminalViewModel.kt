package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.db.entity.TerminalSessionEntity
import com.agon.app.data.model.CommandClassification
import com.agon.app.data.repository.TerminalRepository
import com.agon.app.di.AppContainer
import com.agon.app.sandbox.SandboxManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TerminalLine(
    val kind: Kind,
    val text: String,
    val executionId: Long? = null,
) {
    enum class Kind { PROMPT, STDOUT, STDERR, SYSTEM, META }
}

data class TerminalUiState(
    val session: TerminalSessionEntity? = null,
    val lines: List<TerminalLine> = emptyList(),
    val executions: List<CommandExecutionEntity> = emptyList(),
    val input: String = "",
    val isRunning: Boolean = false,
    val pendingDangerous: String? = null,
    val policyNote: String? = null,
    val error: String? = null,
)

class TerminalViewModel(
    private val sessionId: Long,
    private val sandboxManager: SandboxManager,
    private val terminalRepository: TerminalRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _ui.asStateFlow()

    private var collectJob: Job? = null

    init {
        collectJob = viewModelScope.launch {
            terminalRepository.observeSession(sessionId).collect { session ->
                _ui.update { it.copy(session = session) }
            }
        }
        viewModelScope.launch {
            terminalRepository.observeExecutions(sessionId).collect { execs ->
                _ui.update { state ->
                    // Rebuild transcript from DB + log files for durability
                    state.copy(executions = execs)
                }
                rebuildTranscript(execs)
            }
        }
        viewModelScope.launch {
            val session = terminalRepository.getSession(sessionId)
            _ui.update {
                it.copy(
                    session = session,
                    lines = listOf(
                        TerminalLine(
                            TerminalLine.Kind.SYSTEM,
                            "MusGo-OS CLI · session #$sessionId · cwd=${session?.currentWorkingDirectory ?: "?"}",
                        ),
                        TerminalLine(
                            TerminalLine.Kind.SYSTEM,
                            "Resource limit: 30s CPU wall-clock · sandbox-private cwd only",
                        ),
                    ),
                )
            }
        }
    }

    fun onInputChange(value: String) {
        _ui.update { it.copy(input = value) }
    }

    fun submit() {
        val cmd = _ui.value.input
        if (cmd.isBlank() || _ui.value.isRunning) return

        val policy = sandboxManager.evaluatePolicy(cmd)
        when (policy?.classification) {
            CommandClassification.BLOCKED -> {
                _ui.update {
                    it.copy(
                        lines = it.lines + listOf(
                            TerminalLine(TerminalLine.Kind.PROMPT, "$ $cmd"),
                            TerminalLine(
                                TerminalLine.Kind.STDERR,
                                "blocked: ${policy.description}",
                            ),
                        ),
                        input = "",
                        policyNote = "BLOCKED · ${policy.description}",
                    )
                }
                // Still record via manager
                viewModelScope.launch {
                    runCatching { sandboxManager.executeCommand(sessionId, cmd) }
                }
                return
            }
            CommandClassification.DANGEROUS -> {
                _ui.update {
                    it.copy(
                        pendingDangerous = cmd,
                        policyNote = "DANGEROUS · ${policy.description} — confirm to run",
                    )
                }
                return
            }
            CommandClassification.REVIEW_REQUIRED -> {
                _ui.update {
                    it.copy(policyNote = "REVIEW · ${policy.description}")
                }
            }
            CommandClassification.SAFE -> {
                _ui.update { it.copy(policyNote = "SAFE") }
            }
            null -> {
                _ui.update { it.copy(policyNote = null) }
            }
        }
        runCommand(cmd)
    }

    fun confirmDangerous() {
        val cmd = _ui.value.pendingDangerous ?: return
        _ui.update { it.copy(pendingDangerous = null) }
        runCommand(cmd)
    }

    fun cancelDangerous() {
        _ui.update { it.copy(pendingDangerous = null, policyNote = "Cancelled dangerous command") }
    }

    private fun runCommand(cmd: String) {
        viewModelScope.launch {
            _ui.update {
                it.copy(
                    isRunning = true,
                    input = "",
                    lines = it.lines + TerminalLine(TerminalLine.Kind.PROMPT, "$ $cmd"),
                    error = null,
                )
            }
            try {
                val result = sandboxManager.executeCommand(sessionId, cmd)
                val outLines = mutableListOf<TerminalLine>()
                if (result.stdout.isNotBlank()) {
                    result.stdout.lineSequence().filter { it.isNotEmpty() }.forEach { line ->
                        outLines += TerminalLine(TerminalLine.Kind.STDOUT, line)
                    }
                }
                if (result.stderr.isNotBlank()) {
                    result.stderr.lineSequence().filter { it.isNotEmpty() }.forEach { line ->
                        outLines += TerminalLine(TerminalLine.Kind.STDERR, line)
                    }
                }
                outLines += TerminalLine(
                    TerminalLine.Kind.META,
                    "exit ${result.exitCode} · ${result.durationMs}ms" +
                        if (result.timedOut) " · TIMEOUT" else "",
                )
                _ui.update {
                    it.copy(isRunning = false, lines = it.lines + outLines)
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isRunning = false,
                        lines = it.lines + TerminalLine(
                            TerminalLine.Kind.STDERR,
                            e.message ?: "Execution failed",
                        ),
                        error = e.message,
                    )
                }
            }
        }
    }

    private suspend fun rebuildTranscript(execs: List<CommandExecutionEntity>) {
        // Only used as secondary source; live UI already streams. Keep executions list synced.
        // Optionally hydrate empty UI from disk when returning to session.
        if (_ui.value.lines.size <= 2 && execs.isNotEmpty()) {
            val rebuilt = mutableListOf<TerminalLine>()
            rebuilt += TerminalLine(
                TerminalLine.Kind.SYSTEM,
                "Restored ${execs.size} command(s) from session history",
            )
            for (ex in execs.takeLast(30)) {
                rebuilt += TerminalLine(TerminalLine.Kind.PROMPT, "$ ${ex.commandText}", ex.id)
                val out = terminalRepository.readLogFile(ex.stdoutPath)
                val err = terminalRepository.readLogFile(ex.stderrPath)
                out.lineSequence().filter { it.isNotEmpty() }.forEach {
                    rebuilt += TerminalLine(TerminalLine.Kind.STDOUT, it, ex.id)
                }
                err.lineSequence().filter { it.isNotEmpty() }.forEach {
                    rebuilt += TerminalLine(TerminalLine.Kind.STDERR, it, ex.id)
                }
                rebuilt += TerminalLine(
                    TerminalLine.Kind.META,
                    "exit ${ex.exitCode ?: "?"} · ${ex.durationMs}ms · ${ex.status}",
                    ex.id,
                )
            }
            _ui.update { it.copy(lines = rebuilt) }
        }
    }

    class Factory(
        private val sessionId: Long,
        private val container: AppContainer,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TerminalViewModel(
                sessionId = sessionId,
                sandboxManager = container.sandboxManager,
                terminalRepository = container.terminalRepository,
            ) as T
        }
    }
}
