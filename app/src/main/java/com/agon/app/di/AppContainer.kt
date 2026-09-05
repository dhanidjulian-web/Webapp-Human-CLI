package com.agon.app.di

import android.content.Context
import com.agon.app.data.db.MusGoDatabase
import com.agon.app.data.repository.CommandPolicyRepository
import com.agon.app.data.repository.CredentialRepository
import com.agon.app.data.repository.ProfileRepository
import com.agon.app.data.repository.TerminalRepository
import com.agon.app.sandbox.LocalProcessExecutor
import com.agon.app.sandbox.LocalSandboxManager
import com.agon.app.sandbox.SandboxManager
import com.agon.app.security.CredentialValidator
import com.agon.app.security.SecureCredentialManager

/**
 * Manual DI container — single source of truth for backend services.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: MusGoDatabase = MusGoDatabase.getInstance(appContext)

    val secureCredentialManager: SecureCredentialManager =
        SecureCredentialManager.getInstance()

    val credentialValidator: CredentialValidator = CredentialValidator()

    val credentialRepository: CredentialRepository = CredentialRepository(
        dao = database.credentialDao(),
        crypto = secureCredentialManager,
        validator = credentialValidator,
    )

    val profileRepository: ProfileRepository = ProfileRepository(
        profileDao = database.profileDao(),
        agentDao = database.agentDao(),
    )

    val commandPolicyRepository: CommandPolicyRepository = CommandPolicyRepository(
        dao = database.commandPolicyDao(),
    )

    val terminalRepository: TerminalRepository = TerminalRepository(
        sessionDao = database.terminalSessionDao(),
        executionDao = database.commandExecutionDao(),
    )

    val processExecutor: LocalProcessExecutor = LocalProcessExecutor(appContext)

    val sandboxManager: SandboxManager = LocalSandboxManager(
        context = appContext,
        db = database,
        executor = processExecutor,
    )

    suspend fun ensureDefaults() {
        commandPolicyRepository.ensureSeeded(MusGoDatabase.defaultPolicies())
        if (database.profileDao().count() == 0) {
            profileRepository.createProfile(
                name = "Default",
                description = "Primary MusGo-OS operator profile",
            )
        }
    }
}
