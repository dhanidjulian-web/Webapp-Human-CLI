package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.db.entity.AgentEntity
import com.agon.app.data.db.entity.MAX_AGENTS_PER_PROFILE
import com.agon.app.data.db.entity.ProfileEntity
import com.agon.app.data.model.ModelMode
import com.agon.app.data.repository.ProfileRepository
import com.agon.app.di.AppContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profiles: List<ProfileEntity> = emptyList(),
    val selectedProfileId: Long? = null,
    val agents: List<AgentEntity> = emptyList(),
    val agentCount: Int = 0,
    val maxAgents: Int = MAX_AGENTS_PER_PROFILE,
    val message: String? = null,
    val error: String? = null,
    val isBusy: Boolean = false,
)

class ProfileViewModel(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _selectedId = MutableStateFlow<Long?>(null)
    private val _ui = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _ui.asStateFlow()

    val profiles = repository.observeProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val agents: StateFlow<List<AgentEntity>> = _selectedId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeAgents(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            profiles.collect { list ->
                _ui.update { state ->
                    val sel = state.selectedProfileId ?: list.firstOrNull()?.id
                    if (sel != null && _selectedId.value != sel) {
                        _selectedId.value = sel
                    }
                    state.copy(profiles = list, selectedProfileId = sel)
                }
            }
        }
        viewModelScope.launch {
            agents.collect { list ->
                _ui.update { it.copy(agents = list, agentCount = list.size) }
            }
        }
    }

    fun selectProfile(id: Long) {
        _selectedId.value = id
        _ui.update { it.copy(selectedProfileId = id) }
    }

    fun createProfile(name: String, description: String, mode: ModelMode, modelId: String) {
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                val p = repository.createProfile(name, description, mode, modelId)
                _selectedId.value = p.id
                _ui.update {
                    it.copy(isBusy = false, message = "Profile '${p.name}' created", selectedProfileId = p.id)
                }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteProfile(id)
                _ui.update { it.copy(message = "Profile deleted") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message) }
            }
        }
    }

    fun addAgent(name: String, description: String, prompt: String, modelId: String) {
        val profileId = _selectedId.value ?: return
        viewModelScope.launch {
            _ui.update { it.copy(isBusy = true, error = null) }
            try {
                repository.addAgent(profileId, name, description, prompt, modelId)
                _ui.update { it.copy(isBusy = false, message = "Agent '$name' added") }
            } catch (e: Exception) {
                _ui.update { it.copy(isBusy = false, error = e.message) }
            }
        }
    }

    fun deleteAgent(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteAgent(id)
                _ui.update { it.copy(message = "Agent deleted") }
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
            return ProfileViewModel(container.profileRepository) as T
        }
    }
}
