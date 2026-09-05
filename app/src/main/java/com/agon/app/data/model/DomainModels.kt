package com.agon.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RedactedCredential(
    val id: Long,
    val type: CredentialType,
    val label: String,
    val redactedValue: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastValidatedAt: Long? = null,
    val isValid: Boolean? = null,
    val validationMessage: String? = null,
)

data class CredentialValidationResult(
    val success: Boolean,
    val message: String,
    val metadata: Map<String, String> = emptyMap(),
)

data class ProcessResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val durationMs: Long,
    val stdoutPath: String?,
    val stderrPath: String?,
    val timedOut: Boolean = false,
    val killed: Boolean = false,
)

data class ProcessLine(
    val stream: OutputStreamType,
    val line: String,
    val timestampMs: Long = System.currentTimeMillis(),
)

enum class OutputStreamType {
    STDOUT,
    STDERR,
}

data class SandboxInfo(
    val id: Long,
    val name: String,
    val distro: LinuxDistro,
    val storagePath: String,
    val status: SandboxStatus,
    val activeSessionCount: Int,
    val workDirExists: Boolean,
)

data class CommandPolicyMatch(
    val policyId: Long,
    val pattern: String,
    val classification: CommandClassification,
    val description: String,
)

@Serializable
data class EnvMap(
    val entries: Map<String, String> = emptyMap(),
)
