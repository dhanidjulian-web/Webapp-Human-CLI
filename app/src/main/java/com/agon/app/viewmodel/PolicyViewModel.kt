package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.db.entity.CommandPolicyEntity
import com.agon.app.data.model.CommandClassification
import com.agon.app.data.repository.CommandPolicyRepository
import com.agon.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PolicyUiState(
    val policies: List<CommandPolicyEntity> = emptyList(),
    val message: String? = null,
    val error: String? = null,
)

class PolicyViewModel(
    private val repository: CommandPolicyRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(PolicyUiState())
    val uiState: StateFlow<PolicyUiState> = _ui.asStateFlow()

    val policies = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            policies.collect { list ->
                _ui.update { it.copy(policies = list) }
            }
        }
    }

    fun toggle(entity: CommandPolicyEntity) {
        viewModelScope.launch {
            repository.setEnabled(entity, !entity.enabled)
        }
    }

    fun add(pattern: String, classification: CommandClassification, description: String) {
        viewModelScope.launch {
            try {
                repository.add(pattern, classification, description)
                _ui.update { it.copy(message = "Policy added") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message) }
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
            _ui.update { it.copy(message = "Policy removed") }
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PolicyViewModel(container.commandPolicyRepository) as T
        }
    }
}
