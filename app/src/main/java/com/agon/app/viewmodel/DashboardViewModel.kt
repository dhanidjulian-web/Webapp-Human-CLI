package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.model.RedactedCredential
import com.agon.app.data.repository.CredentialRepository
import com.agon.app.data.repository.ProfileRepository
import com.agon.app.data.repository.TerminalRepository
import com.agon.app.di.AppContainer
import com.agon.app.sandbox.SandboxManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val sandboxCount: Int = 0,
    val runningCount: Int = 0,
    val credentialCount: Int = 0,
    val profileCount: Int = 0,
    val agentCount: Int = 0,
    val recentCommands: List<CommandExecutionEntity> = emptyList(),
    val sandboxes: List<SandboxEntity> = emptyList(),
    val credentials: List<RedactedCredential> = emptyList(),
)

class DashboardViewModel(
    sandboxManager: SandboxManager,
    credentialRepository: CredentialRepository,
    profileRepository: ProfileRepository,
    terminalRepository: TerminalRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        sandboxManager.observeSandboxes(),
        credentialRepository.observeRedacted(),
        profileRepository.observeProfiles(),
        profileRepository.observeAllAgents(),
        terminalRepository.observeRecentExecutions(20),
    ) { sandboxes, creds, profiles, agents, commands ->
        DashboardUiState(
            sandboxCount = sandboxes.size,
            runningCount = sandboxes.count {
                it.status == com.agon.app.data.model.SandboxStatus.RUNNING
            },
            credentialCount = creds.size,
            profileCount = profiles.size,
            agentCount = agents.size,
            recentCommands = commands,
            sandboxes = sandboxes,
            credentials = creds,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(
                sandboxManager = container.sandboxManager,
                credentialRepository = container.credentialRepository,
                profileRepository = container.profileRepository,
                terminalRepository = container.terminalRepository,
            ) as T
        }
    }
}
