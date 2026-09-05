package com.agon.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.chatDataStore by preferencesDataStore(name = "chat_history_store")

class ChatRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val HISTORY_KEY = stringPreferencesKey("chat_messages_json")

    val chatMessagesFlow: Flow<List<ChatMessage>> = context.chatDataStore.data.map { prefs ->
        val jsonStr = prefs[HISTORY_KEY] ?: return@map defaultWelcomeMessages()
        try {
            val list = json.decodeFromString<List<ChatMessage>>(jsonStr)
            if (list.isEmpty()) defaultWelcomeMessages() else list
        } catch (e: Exception) {
            defaultWelcomeMessages()
        }
    }

    suspend fun saveMessages(messages: List<ChatMessage>) {
        context.chatDataStore.edit { prefs ->
            prefs[HISTORY_KEY] = json.encodeToString(messages)
        }
    }

    suspend fun clearHistory() {
        context.chatDataStore.edit { prefs ->
            prefs[HISTORY_KEY] = json.encodeToString(defaultWelcomeMessages())
        }
    }

    private fun defaultWelcomeMessages(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                sender = MessageSender.AI,
                content = "Selamat datang di **KodeAI Code Assistant**! 🚀\n\nSaya adalah asisten kecerdasan buatan khusus pemrograman. Saya siap membantu Anda menulis kode, refactoring, debugging, dan menganalisis berkas proyek.\n\n✨ **Fitur Utama:**\n- 🔌 **BYOK Specification**: Masukkan 1 atau beberapa API Key per provider.\n- 📊 **Spec Free Providers**: Groq, Gemini, DeepSeek, SambaNova, Cerebras, OpenRouter, Together, DLL.\n- 💾 **Local Model Spec**: Mount file `.gguf` lokal & jalankan offline.\n- 🤖 **Ragmy AI Web Agent**: Integrasi langsung dengan agent URL `chat.ragmyai.com`.\n- 📞 **Live Voice Chat AI**: Fitur telepon interaktif dengan suara AI real-time.\n- ✨ **REAL AI Suggest**: Autodetek otomatis input & tampilkan 3 saran teratas!",
                providerName = "Groq",
                modelName = "Llama-3.3-70B"
            )
        )
    }
}
