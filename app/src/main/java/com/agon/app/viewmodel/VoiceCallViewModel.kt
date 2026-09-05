package com.agon.app.viewmodel

import android.app.Application
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.repository.AiServiceRepository
import com.agon.app.data.repository.ApiKeyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

enum class CallState {
    IDLE, CONNECTING, CONNECTED, LISTENING, THINKING, SPEAKING, ENDED
}

data class CallTranscript(
    val id: String = java.util.UUID.randomUUID().toString(),
    val speaker: String, // User or AI
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class VoiceCallViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val apiKeyRepository = ApiKeyRepository(application)
    private val aiServiceRepository = AiServiceRepository()

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _transcripts = MutableStateFlow<List<CallTranscript>>(emptyList())
    val transcripts: StateFlow<List<CallTranscript>> = _transcripts.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(true)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private val _selectedPersona = MutableStateFlow("Senior Architect")
    val selectedPersona: StateFlow<String> = _selectedPersona.asStateFlow()

    private val _audioWaveLevel = MutableStateFlow(0.3f)
    val audioWaveLevel: StateFlow<Float> = _audioWaveLevel.asStateFlow()

    private val _currentSpokenText = MutableStateFlow("")
    val currentSpokenText: StateFlow<String> = _currentSpokenText.asStateFlow()

    init {
        tts = TextToSpeech(application, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.US
            }
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {
                    _callState.value = CallState.LISTENING
                }
                override fun onRmsChanged(rmsdB: Float) {
                    val level = (rmsdB / 10f).coerceIn(0.1f, 1.0f)
                    _audioWaveLevel.value = level
                }
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _callState.value = CallState.THINKING
                }
                override fun onError(error: Int) {
                    if (_callState.value == CallState.LISTENING) {
                        _callState.value = CallState.CONNECTED
                    }
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        onUserSpoke(text)
                    } else {
                        _callState.value = CallState.CONNECTED
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _currentSpokenText.value = matches[0]
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun startCall() {
        viewModelScope.launch {
            _callState.value = CallState.CONNECTING
            _transcripts.value = emptyList()
            kotlinx.coroutines.delay(1000)

            _callState.value = CallState.CONNECTED

            val greeting = when (_selectedPersona.value) {
                "Senior Architect" -> "Halo! Saya Senior Software Architect KodeAI. Ada arsitektur sistem atau kode yang ingin kita diskusikan?"
                "Code Reviewer" -> "Halo! Siap melakukan code review. Silakan jelaskan bagian kode atau modul yang ingin diperiksa."
                "Quick Debugger" -> "Halo! Sebutkan error atau bug yang Anda temui, mari kita cari solusinya bersama."
                else -> "Halo! Saya mentor AI Anda. Ada topik pemrograman yang ingin Anda tanyakan hari ini?"
            }

            speakAiResponse(greeting)
        }
    }

    fun endCall() {
        tts?.stop()
        speechRecognizer?.stopListening()
        _callState.value = CallState.ENDED
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            speechRecognizer?.stopListening()
        }
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun selectPersona(persona: String) {
        _selectedPersona.value = persona
    }

    fun listenToUserSpeech() {
        if (_isMuted.value || _callState.value == CallState.SPEAKING) return

        _currentSpokenText.value = ""
        _callState.value = CallState.LISTENING

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            _callState.value = CallState.CONNECTED
        }
    }

    private fun onUserSpoke(spokenText: String) {
        if (spokenText.isBlank()) {
            _callState.value = CallState.CONNECTED
            return
        }

        val userTranscript = CallTranscript(speaker = "Anda", text = spokenText)
        _transcripts.value = _transcripts.value + userTranscript
        _currentSpokenText.value = ""

        _callState.value = CallState.THINKING

        viewModelScope.launch {
            val provider = "groq"
            val model = "llama-3.3-70b-versatile"

            val personaSystem = when (_selectedPersona.value) {
                "Senior Architect" -> "Anda adalah Senior Software Architect. Berikan jawaban teknis yang ringkas, ringkas, dan to the point dalam Bahasa Indonesia."
                "Code Reviewer" -> "Anda adalah Code Reviewer senior. Fokus pada analisis bug dan best practices."
                "Quick Debugger" -> "Anda adalah Debugger cepat. Langsung berikan solusi perbaikan bug secara ringkas."
                else -> "Anda adalah mentor pemrograman yang ramah. Jawab dengan kalimat percakapan lisan yang singkat dan mudah dipahami."
            }

            val result = aiServiceRepository.generateCompletion(
                providerId = provider,
                modelId = model,
                apiKey = "",
                systemPrompt = personaSystem,
                userPrompt = "User sedang menelepon Anda dan berkata: \"$spokenText\". Jawab dengan ringkas (maksimal 2-3 kalimat) agar nyaman didengar lewat percakapan telepon."
            )

            val reply = if (result.isSuccess) {
                result.getOrNull()?.replace(Regex("```[a-z]*\n[\\s\\S]*?\n```"), "kode program telah saya tampilkan di layar.") ?: "Siap, ada lagi yang ingin ditanyakan?"
            } else {
                "Maaf, koneksi AI terputus sejenak. Silakan ulangi pertanyaan Anda."
            }

            speakAiResponse(reply)
        }
    }

    private fun speakAiResponse(text: String) {
        val cleanText = text.replace("*", "").replace("#", "").trim()
        val aiTranscript = CallTranscript(speaker = "KodeAI (${_selectedPersona.value})", text = cleanText)
        _transcripts.value = _transcripts.value + aiTranscript

        _callState.value = CallState.SPEAKING

        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _callState.value = CallState.SPEAKING
            }
            override fun onDone(utteranceId: String?) {
                _callState.value = CallState.CONNECTED
            }
            override fun onError(utteranceId: String?) {
                _callState.value = CallState.CONNECTED
            }
        })

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ai_utterance_${System.currentTimeMillis()}")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "ai_utterance_${System.currentTimeMillis()}")
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
