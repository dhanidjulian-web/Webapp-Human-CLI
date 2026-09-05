package com.agon.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agon.app.data.model.GgufModelInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.ggufDataStore by preferencesDataStore(name = "gguf_models_store")

class GgufRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val GGUF_CONFIG_KEY = stringPreferencesKey("active_gguf_config")

    val ggufConfigFlow: Flow<GgufModelInfo?> = context.ggufDataStore.data.map { prefs ->
        val jsonStr = prefs[GGUF_CONFIG_KEY] ?: return@map null
        try {
            json.decodeFromString<GgufModelInfo>(jsonStr)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveGgufConfig(config: GgufModelInfo) {
        context.ggufDataStore.edit { prefs ->
            prefs[GGUF_CONFIG_KEY] = json.encodeToString(config)
        }
    }

    suspend fun unmountModel() {
        context.ggufDataStore.edit { prefs ->
            val currentStr = prefs[GGUF_CONFIG_KEY]
            if (currentStr != null) {
                try {
                    val config = json.decodeFromString<GgufModelInfo>(currentStr)
                    prefs[GGUF_CONFIG_KEY] = json.encodeToString(config.copy(isMounted = false))
                } catch (e: Exception) {
                    prefs.remove(GGUF_CONFIG_KEY)
                }
            }
        }
    }

    fun estimateRamNeededGB(fileSizeMB: Long, contextWindow: Int): Double {
        val baseFileGB = fileSizeMB / 1024.0
        val kvCacheGB = (contextWindow / 2048.0) * 0.45
        val overheadGB = 0.5
        return (baseFileGB + kvCacheGB + overheadGB).let { Math.round(it * 10.0) / 10.0 }
    }
}
