package com.agon.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class MessageSender {
    USER, AI, SYSTEM
}

@Serializable
data class FileAttachment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sizeFormatted: String,
    val mimeType: String,
    val extension: String,
    val content: String = "",
    val uriString: String = ""
)

@Serializable
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val providerName: String = "Groq",
    val modelName: String = "Llama-3.3-70B",
    val attachments: List<FileAttachment> = emptyList(),
    val isCode: Boolean = false,
    val detectedLanguage: String = "",
    val tokensUsed: Int = 0,
    val isError: Boolean = false,
    val isStreaming: Boolean = false
)

@Serializable
data class ChatSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val providerId: String,
    val modelId: String,
    val messages: List<ChatMessage> = emptyList()
)
