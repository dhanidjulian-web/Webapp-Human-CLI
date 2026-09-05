package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.model.LinuxDistro
import com.agon.app.data.model.SandboxStatus
import com.agon.app.di.AppContainer
import com.agon.app.sandbox.SandboxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SandboxListUiState(
    val sandboxes: List<SandboxEntity> = emptyList(),
    val isBusy: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastOpenedSessionId: Long? = null,
    val lastOpenedSandboxId: Long? = null,
)

class SandboxViewModel(
    private val sandboxManager: SandboxManager,
) : ViewModel() {

    private val _ui = MutableStateFlow(SandboxListUiState())
    val uiState: StateFlow<SandboxListUiState> = _ui.asStateFlow()

    val sandboxes: StateFlow<List<SandboxEntity>> =
        sandboxManager.observeSandboxes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            sandboxes.collect { list ->
                _ui.update { it.copy(sandboxes = list) }
            }
        }
    }

    fun create(name: String, distro: LinuxDistro) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null, message = null) }
            try {
                val sb = sandboxManager.create(name, distro)
                sandboxManager.initialize(sb.id)
                _ui.update {
                    it.copy(isBusy = false, message = "Sandbox '${sb.name}' created & initialized")
                }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message ?: "Create failed") }
            }
        }
    }

    fun initialize(id: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                sandboxManager.initialize(id)
                _ui.update { it.copy(isBusy = false, message = "Initialized #$id") }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun stop(id: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                sandboxManager.stop(id)
                _ui.update { it.copy(isBusy = false, message = "Stopped #$id") }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun destroy(id: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                sandboxManager.destroy(id)
                _ui.update { it.copy(isBusy = false, message = "Destroyed runtime #$id (exports preserved)") }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun openTerminal(sandboxId: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                val sb = sandboxes.value.find { it.id == sandboxId }
                if (sb?.status == SandboxStatus.STOPPED || sb?.status == SandboxStatus.CREATED) {
                    runCatching { sandboxManager.initialize(sandboxId) }
                }
                val sessionId = sandboxManager.openSession(sandboxId)
                _ui.update {
                    it.copy(
                        isBusy = false,
                        lastOpenedSessionId = sessionId,
                        lastOpenedSandboxId = sandboxId,
                        message = "Session #$sessionId opened",
                    )
                }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun consumeNavigation() {
        _ui.update { it.copy(lastOpenedSessionId = null, lastOpenedSandboxId = null) }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SandboxViewModel(container.sandboxManager) as T
        }
    }
}
