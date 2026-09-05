package com.agon.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agon.app.data.model.ApiKeyConfig
import com.agon.app.data.model.ApiKeyStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "byok_keys_store")

class ApiKeyRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val KEYS_STORAGE_KEY = stringPreferencesKey("byok_keys_json")
    private val ACTIVE_PROVIDER_KEY = stringPreferencesKey("active_provider_id")
    private val ACTIVE_MODEL_KEY = stringPreferencesKey("active_model_id")

    val apiKeysFlow: Flow<List<ApiKeyConfig>> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[KEYS_STORAGE_KEY] ?: "[]"
        try {
            json.decodeFromString<List<ApiKeyConfig>>(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val activeProviderIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ACTIVE_PROVIDER_KEY] ?: "groq"
    }

    val activeModelIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ACTIVE_MODEL_KEY] ?: "llama-3.3-70b-versatile"
    }

    suspend fun saveApiKey(config: ApiKeyConfig) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEYS_STORAGE_KEY] ?: "[]"
            val currentList = try {
                json.decodeFromString<List<ApiKeyConfig>>(jsonStr).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            // Deactivate other keys for same provider if this is set active
            val updatedList = currentList.map {
                if (it.providerId == config.providerId && config.isActive) {
                    it.copy(isActive = false)
                } else {
                    it
                }
            }.toMutableList()

            val existingIndex = updatedList.indexOfFirst { it.id == config.id }
            if (existingIndex >= 0) {
                updatedList[existingIndex] = config
            } else {
                updatedList.add(config)
            }

            prefs[KEYS_STORAGE_KEY] = json.encodeToString(updatedList)
        }
    }

    suspend fun deleteApiKey(keyId: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEYS_STORAGE_KEY] ?: "[]"
            val currentList = try {
                json.decodeFromString<List<ApiKeyConfig>>(jsonStr).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            currentList.removeAll { it.id == keyId }
            prefs[KEYS_STORAGE_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun setActiveKey(providerId: String, keyId: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEYS_STORAGE_KEY] ?: "[]"
            val currentList = try {
                json.decodeFromString<List<ApiKeyConfig>>(jsonStr).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            val updatedList = currentList.map {
                if (it.providerId == providerId) {
                    it.copy(isActive = (it.id == keyId))
                } else {
                    it
                }
            }
            prefs[KEYS_STORAGE_KEY] = json.encodeToString(updatedList)
        }
    }

    suspend fun updateKeyStatus(keyId: String, status: ApiKeyStatus) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEYS_STORAGE_KEY] ?: "[]"
            val currentList = try {
                json.decodeFromString<List<ApiKeyConfig>>(jsonStr).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            val updatedList = currentList.map {
                if (it.id == keyId) {
                    it.copy(status = status, lastTestedTimestamp = System.currentTimeMillis())
                } else {
                    it
                }
            }
            prefs[KEYS_STORAGE_KEY] = json.encodeToString(updatedList)
        }
    }

    suspend fun setActiveProviderAndModel(providerId: String, modelId: String) {
        context.dataStore.edit { prefs ->
            prefs[ACTIVE_PROVIDER_KEY] = providerId
            prefs[ACTIVE_MODEL_KEY] = modelId
        }
    }
}
