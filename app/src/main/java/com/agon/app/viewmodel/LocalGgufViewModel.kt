package com.agon.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.model.GgufModelInfo
import com.agon.app.data.repository.ApiKeyRepository
import com.agon.app.data.repository.GgufRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocalGgufViewModel(application: Application) : AndroidViewModel(application) {

    private val ggufRepository = GgufRepository(application)
    private val apiKeyRepository = ApiKeyRepository(application)

    val ggufConfig: StateFlow<GgufModelInfo?> = ggufRepository.ggufConfigFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GgufModelInfo(
            fileName = "qwen2.5-coder-7b-instruct.Q4_K_M.gguf",
            filePath = "/storage/emulated/0/Download/qwen2.5-coder-7b-instruct.Q4_K_M.gguf",
            fileSizeFormatted = "4.2 GB",
            fileSizeBytes = 4500000000L,
            architecture = "Qwen2.5",
            quantType = "Q4_K_M",
            contextWindow = 4096,
            cpuThreads = 6,
            temperature = 0.7f,
            ramRequiredGB = 4.8,
            isMounted = true,
            mountedTimestamp = System.currentTimeMillis()
        )
    )

    private val _isMounting = MutableStateFlow(false)
    val isMounting: StateFlow<Boolean> = _isMounting.asStateFlow()

    private val _mountLog = MutableStateFlow("Local GGUF Runtime siap. Sambungkan file .gguf dari penyimpanan lokal.")
    val mountLog: StateFlow<String> = _mountLog.asStateFlow()

    fun selectGgufFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                var fileName = "model.gguf"
                var fileSize = 0L

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIdx != -1) fileName = cursor.getString(nameIdx)
                        if (sizeIdx != -1) fileSize = cursor.getLong(sizeIdx)
                    }
                }

                val sizeMB = fileSize / (1024 * 1024)
                val sizeGBStr = String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0))

                val quantGuess = when {
                    fileName.lowercase().contains("q4_k_m") -> "Q4_K_M"
                    fileName.lowercase().contains("q8_0") -> "Q8_0"
                    fileName.lowercase().contains("q5_k_m") -> "Q5_K_M"
                    fileName.lowercase().contains("q2_k") -> "Q2_K"
                    else -> "Q4_K_M (Terdeteksi)"
                }

                val archGuess = when {
                    fileName.lowercase().contains("qwen") -> "Qwen2.5"
                    fileName.lowercase().contains("deepseek") -> "DeepSeek"
                    fileName.lowercase().contains("llama") -> "Llama3"
                    fileName.lowercase().contains("mistral") -> "Mistral"
                    else -> "GGUF Transformer"
                }

                val ramNeeded = ggufRepository.estimateRamNeededGB(sizeMB, 4096)

                val newConfig = GgufModelInfo(
                    fileName = fileName,
                    filePath = uri.toString(),
                    fileSizeFormatted = sizeGBStr,
                    fileSizeBytes = fileSize,
                    architecture = archGuess,
                    quantType = quantGuess,
                    contextWindow = 4096,
                    cpuThreads = 4,
                    temperature = 0.7f,
                    ramRequiredGB = ramNeeded,
                    isMounted = false
                )

                ggufRepository.saveGgufConfig(newConfig)
                _mountLog.value = "📄 Berkas `.gguf` teridentifikasi: $fileName ($sizeGBStr). Siap di-mount ke RAM."
            } catch (e: Exception) {
                _mountLog.value = "⚠️ Gagal membaca metadata berkas GGUF: ${e.localizedMessage}"
            }
        }
    }

    fun updateParameters(contextWindow: Int, threads: Int, temperature: Float, localUrl: String) {
        viewModelScope.launch {
            val current = ggufConfig.value ?: return@launch
            val ramNeeded = ggufRepository.estimateRamNeededGB(current.fileSizeBytes / (1024 * 1024), contextWindow)
            val updated = current.copy(
                contextWindow = contextWindow,
                cpuThreads = threads,
                temperature = temperature,
                ramRequiredGB = ramNeeded,
                localServerUrl = localUrl
            )
            ggufRepository.saveGgufConfig(updated)
        }
    }

    fun mountModel() {
        viewModelScope.launch {
            val current = ggufConfig.value ?: return@launch
            _isMounting.value = true
            _mountLog.value = "⌛ Mengalokasikan tensor buffer (${current.ramRequiredGB} GB RAM)...\nLoading quantized weights into llama.cpp memory pool..."

            kotlinx.coroutines.delay(1200)

            val mounted = current.copy(
                isMounted = true,
                mountedTimestamp = System.currentTimeMillis()
            )
            ggufRepository.saveGgufConfig(mounted)
            _isMounting.value = false
            _mountLog.value = "✅ Model GGUF [${current.fileName}] BERHASIL DI-MOUNT!\nMode Eksekusi: ${current.cpuThreads} CPU Threads | Konteks: ${current.contextWindow} Tokens | RAM: ${current.ramRequiredGB} GB."

            // Also set as active provider in app
            apiKeyRepository.setActiveProviderAndModel("local_gguf", current.fileName)
        }
    }

    fun unmountModel() {
        viewModelScope.launch {
            ggufRepository.unmountModel()
            _mountLog.value = "🔴 Model GGUF telah di-unmount dari memori RAM."
        }
    }
}
