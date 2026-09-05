package com.agon.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProviderSpec(
    val id: String,
    val name: String,
    val tagline: String,
    val freeTierDetails: String,
    val rateLimitInfo: String,
    val latencyMs: Int,
    val contextWindow: String,
    val supportsVision: Boolean,
    val supportsCode: Boolean,
    val speedScore: Double, // tok/s
    val codeScore: Int, // 1-100 rating
    val availableModels: List<String>,
    val defaultModel: String,
    val websiteUrl: String,
    val apiKeyUrl: String,
    val isBYOKSupported: Boolean = true,
    val isLocal: Boolean = false,
    val category: String = "Freemium" // Free, Freemium, Local
)

@Serializable
enum class ApiKeyStatus {
    ACTIVE, INVALID, QUOTA_EXCEEDED, UNTESTED
}

@Serializable
data class ApiKeyConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val providerId: String,
    val apiKey: String,
    val label: String,
    val isActive: Boolean = true,
    val dateAdded: Long = System.currentTimeMillis(),
    val status: ApiKeyStatus = ApiKeyStatus.UNTESTED,
    val lastTestedTimestamp: Long = 0,
    val endpointUrl: String = ""
)

@Serializable
data class GgufModelInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fileName: String,
    val filePath: String,
    val fileSizeFormatted: String,
    val fileSizeBytes: Long,
    val architecture: String = "Llama", // Llama, Qwen, DeepSeek, Phi, Mistral
    val quantType: String = "Q4_K_M",
    val contextWindow: Int = 4096,
    val cpuThreads: Int = 4,
    val temperature: Float = 0.7f,
    val ramRequiredGB: Double = 4.2,
    val isMounted: Boolean = false,
    val mountedTimestamp: Long = 0,
    val localServerUrl: String = "http://localhost:11434"
)

data class AiSuggestItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val promptText: String,
    val iconName: String = "Code"
)
