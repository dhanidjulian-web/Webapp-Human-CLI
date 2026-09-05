package com.agon.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.model.AiSuggestItem
import com.agon.app.data.model.ApiKeyConfig
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.FileAttachment
import com.agon.app.data.model.MessageSender
import com.agon.app.data.repository.AiServiceRepository
import com.agon.app.data.repository.ApiKeyRepository
import com.agon.app.data.repository.ChatRepository
import com.agon.app.data.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKeyRepository = ApiKeyRepository(application)
    private val chatRepository = ChatRepository(application)
    private val aiServiceRepository = AiServiceRepository()

    val messages: StateFlow<List<ChatMessage>> = chatRepository.chatMessagesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeProviderId = apiKeyRepository.activeProviderIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "groq"
    )

    val activeModelId = apiKeyRepository.activeModelIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "llama-3.3-70b-versatile"
    )

    val apiKeys = apiKeyRepository.apiKeysFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _attachedFiles = MutableStateFlow<List<FileAttachment>>(emptyList())
    val attachedFiles: StateFlow<List<FileAttachment>> = _attachedFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showSuggestMenu = MutableStateFlow(false)
    val showSuggestMenu: StateFlow<Boolean> = _showSuggestMenu.asStateFlow()

    private val _suggestions = MutableStateFlow<List<AiSuggestItem>>(emptyList())
    val suggestions: StateFlow<List<AiSuggestItem>> = _suggestions.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    init {
        updateSuggestions("")
    }

    fun onInputTextChange(newText: String) {
        _inputText.value = newText
        updateSuggestions(newText)
    }

    fun toggleSuggestMenu() {
        _showSuggestMenu.value = !_showSuggestMenu.value
        updateSuggestions(_inputText.value)
    }

    private fun updateSuggestions(input: String) {
        val lower = input.lowercase().trim()
        val list = mutableListOf<AiSuggestItem>()

        if (lower.isEmpty() && _attachedFiles.value.isEmpty()) {
            list.add(AiSuggestItem(title = "🚀 Buat Rest API Kotlin", subtitle = "Generate controller & data class", promptText = "Buat REST API Controller lengkap dengan Ktor / Retrofit data class dalam Bahasa Kotlin."))
            list.add(AiSuggestItem(title = "🐛 Fix Bug & Refactor", subtitle = "Optimasi clean code & error handling", promptText = "Tolong periksa kode saya ini, cari bug potensial dan refactor menjadi clean code."))
            list.add(AiSuggestItem(title = "🧪 Buat Unit Test JUnit5", subtitle = "Test case scenario & mock testing", promptText = "Buat Unit Test komprehensif menggunakan JUnit5 dan Mockk untuk fungsi berikut."))
        } else if (_attachedFiles.value.isNotEmpty()) {
            val fileName = _attachedFiles.value.first().name
            list.add(AiSuggestItem(title = "📄 Analisis & Review $fileName", subtitle = "Evaluasi arsitektur dan bugs", promptText = "Analisis file $fileName ini secara menyeluruh, jelaskan alur logika dan poin perbaikan yang direkomendasikan."))
            list.add(AiSuggestItem(title = "⚡ Optimasi Kinerja Kode", subtitle = "Tingkatkan kecepatan & efisiensi RAM", promptText = "Bagaimana cara mengoptimalkan performa dan penggunaan memori pada kode file $fileName ini?"))
            list.add(AiSuggestItem(title = "📝 Tambahkan Dokumentasi KDocs", subtitle = "Generate dokumentasi & komentar", promptText = "Tambahkan KDocs / Javadoc komentar penjelasan lengkap pada semua fungsi di file $fileName."))
        } else if (lower.contains("fun") || lower.contains("class") || lower.contains("code") || lower.contains("val ") || lower.contains("def ")) {
            list.add(AiSuggestItem(title = "🔍 Refactor Ke Clean Architecture", subtitle = "Pisahkan UI, ViewModel & Repository", promptText = "Refactor kode ini ke dalam pola Clean Architecture (Domain, Data, UI Layer)."))
            list.add(AiSuggestItem(title = "🛡️ Tambah Robust Error Handling", subtitle = "Bungkus try-catch & Result type", promptText = "Tambahkan penanganan error yang kuat (Try-Catch / Result state) pada kode berikut."))
            list.add(AiSuggestItem(title = "🚀 Konversi ke Coroutine/Async", subtitle = "Ubah callback ke suspend function", promptText = "Ubah kode berikut agar mendukung Coroutines asynchronous `suspend fun`."))
        } else {
            list.add(AiSuggestItem(title = "💡 Berikan Contoh Kode Praktis", subtitle = "Implementasi langsung siap pakai", promptText = "Berikan contoh kode praktis dan lengkap untuk: $input"))
            list.add(AiSuggestItem(title = "📖 Jelaskan Cara Kerja Baris Demi Baris", subtitle = "Penjelasan detail konsep teknis", promptText = "Tolong jelaskan secara mendalam dan baris demi baris konsep serta implementasi dari: $input"))
            list.add(AiSuggestItem(title = "⚔️ Bandingkan 2 Framework / Pendekatan", subtitle = "Kelebihan, kekurangan & saran benchmark", promptText = "Bandingkan pendekatan teknis, kelebihan, dan kekurangan untuk topik: $input"))
        }

        // Always top 3 items as required!
        _suggestions.value = list.take(3)
    }

    fun applySuggestion(item: AiSuggestItem) {
        _inputText.value = item.promptText
        _showSuggestMenu.value = false
    }

    fun addFileAttachment(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                var fileName = "file_${System.currentTimeMillis()}"
                var fileSize = 0L

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }

                val ext = if (fileName.contains(".")) ".${fileName.substringAfterLast(".")}" else ".txt"
                val mimeType = context.contentResolver.getType(uri) ?: "text/plain"

                // Read text content if readable
                val content = try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.bufferedReader().use { it.readText() }
                    } ?: ""
                } catch (e: Exception) {
                    "[File Biner / Gambar: ${fileName}]"
                }

                val formattedSize = if (fileSize > 1024 * 1024) "${fileSize / (1024 * 1024)} MB" else "${fileSize / 1024} KB"

                val attachment = FileAttachment(
                    name = fileName,
                    sizeFormatted = formattedSize,
                    mimeType = mimeType,
                    extension = ext,
                    content = content.take(50000), // max 50k chars
                    uriString = uri.toString()
                )

                _attachedFiles.value = _attachedFiles.value + attachment
                updateSuggestions(_inputText.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeAttachment(attachmentId: String) {
        _attachedFiles.value = _attachedFiles.value.filter { it.id != attachmentId }
        updateSuggestions(_inputText.value)
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        val currentAttachments = _attachedFiles.value.toList()

        if (text.isEmpty() && currentAttachments.isEmpty()) return

        val provider = activeProviderId.value
        val model = activeModelId.value

        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            content = text,
            attachments = currentAttachments,
            providerName = provider,
            modelName = model
        )

        val updatedMessages = messages.value + userMsg
        viewModelScope.launch {
            chatRepository.saveMessages(updatedMessages)
        }

        _inputText.value = ""
        _attachedFiles.value = emptyList()
        _isLoading.value = true

        viewModelScope.launch {
            // Find active key for this provider
            val keys = apiKeys.value.filter { it.providerId == provider }
            val activeKeyConfig = keys.find { it.isActive } ?: keys.firstOrNull()
            val apiKeyStr = activeKeyConfig?.apiKey ?: ""
            val customEndpoint = activeKeyConfig?.endpointUrl ?: ""

            val historyPairs = updatedMessages.takeLast(10).map {
                (if (it.sender == MessageSender.USER) "USER" else "AI") to it.content
            }

            val result = aiServiceRepository.generateCompletion(
                providerId = provider,
                modelId = model,
                apiKey = apiKeyStr,
                customEndpoint = customEndpoint,
                systemPrompt = "Anda adalah KodeAI, Code Assistant cerdas berbasis Bahasa Indonesia.",
                userPrompt = text,
                fileAttachments = currentAttachments,
                chatHistory = historyPairs
            )

            _isLoading.value = false

            val aiMsg = if (result.isSuccess) {
                val reply = result.getOrNull() ?: ""
                ChatMessage(
                    sender = MessageSender.AI,
                    content = reply,
                    providerName = provider.uppercase(),
                    modelName = model,
                    isCode = reply.contains("```")
                )
            } else {
                ChatMessage(
                    sender = MessageSender.AI,
                    content = "⚠️ Gagal mendapatkan respons: ${result.exceptionOrNull()?.localizedMessage}\n\nSilakan periksa API Key Anda pada tab **BYOK Provider**.",
                    providerName = provider.uppercase(),
                    modelName = model,
                    isError = true
                )
            }

            chatRepository.saveMessages(messages.value + aiMsg)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatRepository.clearHistory()
        }
    }

    fun setVoiceInputText(spokenText: String) {
        _inputText.value = spokenText
        updateSuggestions(spokenText)
    }

    fun setProviderAndModel(providerId: String, modelId: String) {
        viewModelScope.launch {
            apiKeyRepository.setActiveProviderAndModel(providerId, modelId)
        }
    }
}
