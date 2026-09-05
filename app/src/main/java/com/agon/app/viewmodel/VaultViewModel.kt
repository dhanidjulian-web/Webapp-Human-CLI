package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.model.CredentialType
import com.agon.app.data.model.RedactedCredential
import com.agon.app.data.repository.CredentialRepository
import com.agon.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VaultUiState(
    val credentials: List<RedactedCredential> = emptyList(),
    val isSaving: Boolean = false,
    val isValidatingId: Long? = null,
    val message: String? = null,
    val error: String? = null,
)

class VaultViewModel(
    private val repository: CredentialRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _ui.asStateFlow()

    val credentials: StateFlow<List<RedactedCredential>> =
        repository.observeRedacted()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            credentials.collect { list ->
                _ui.update { it.copy(credentials = list) }
            }
        }
    }

    fun save(type: CredentialType, label: String, secret: String) {
        viewModelScope.launch {
            _ui.update { it.copy(isSaving = true, error = null, message = null) }
            try {
                val stored = repository.store(type, label, secret)
                _ui.update {
                    it.copy(
                        isSaving = false,
                        message = "Stored ${stored.type.displayName} as ${stored.redactedValue}",
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(isSaving = false, error = e.message ?: "Save failed")
                }
            }
        }
    }

    fun validate(id: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isValidatingId = id, error = null, message = null) }
            try {
                val result = repository.validate(id)
                _ui.update {
                    it.copy(
                        isValidatingId = null,
                        message = result.message,
                        error = if (!result.success) result.message else null,
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isValidatingId = null,
                        error = e.message ?: "Validation failed",
                    )
                }
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                _ui.update { it.copy(message = "Credential deleted") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VaultViewModel(container.credentialRepository) as T
        }
    }
}
