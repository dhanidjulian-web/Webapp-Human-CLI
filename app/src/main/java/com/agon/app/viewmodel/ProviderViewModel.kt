package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.model.ApiKeyConfig
import com.agon.app.data.model.ApiKeyStatus
import com.agon.app.data.model.ProviderSpec
import com.agon.app.data.repository.AiServiceRepository
import com.agon.app.data.repository.ApiKeyRepository
import com.agon.app.data.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKeyRepository = ApiKeyRepository(application)
    private val aiServiceRepository = AiServiceRepository()

    val providers: List<ProviderSpec> = ProviderRepository.PROVIDERS

    val apiKeys: StateFlow<List<ApiKeyConfig>> = apiKeyRepository.apiKeysFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeProviderId: StateFlow<String> = apiKeyRepository.activeProviderIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "groq"
    )

    val activeModelId: StateFlow<String> = apiKeyRepository.activeModelIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "llama-3.3-70b-versatile"
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua") // Semua, Gratis, Freemium, Lokal
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _testingKeyId = MutableStateFlow<String?>(null)
    val testingKeyId: StateFlow<String?> = _testingKeyId.asStateFlow()

    val filteredProviders: StateFlow<List<ProviderSpec>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, cat ->
        providers.filter { provider ->
            val matchesQuery = provider.name.contains(query, ignoreCase = true) ||
                    provider.tagline.contains(query, ignoreCase = true) ||
                    provider.availableModels.any { it.contains(query, ignoreCase = true) }
            val matchesCategory = (cat == "Semua") || (provider.category.equals(cat, ignoreCase = true))
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = providers
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
    }

    fun saveApiKey(providerId: String, apiKey: String, label: String, endpointUrl: String = "", isActive: Boolean = true) {
        viewModelScope.launch {
            val keyConfig = ApiKeyConfig(
                providerId = providerId,
                apiKey = apiKey.trim(),
                label = label.ifEmpty { "API Key ${System.currentTimeMillis().toString().takeLast(4)}" },
                endpointUrl = endpointUrl.trim(),
                isActive = isActive,
                status = ApiKeyStatus.UNTESTED
            )
            apiKeyRepository.saveApiKey(keyConfig)
        }
    }

    fun deleteApiKey(keyId: String) {
        viewModelScope.launch {
            apiKeyRepository.deleteApiKey(keyId)
        }
    }

    fun setActiveKey(providerId: String, keyId: String) {
        viewModelScope.launch {
            apiKeyRepository.setActiveKey(providerId, keyId)
        }
    }

    fun testKeyConnection(keyConfig: ApiKeyConfig) {
        viewModelScope.launch {
            _testingKeyId.value = keyConfig.id
            val success = aiServiceRepository.testApiKey(
                providerId = keyConfig.providerId,
                apiKey = keyConfig.apiKey,
                customEndpoint = keyConfig.endpointUrl
            )
            _testingKeyId.value = null
            apiKeyRepository.updateKeyStatus(
                keyId = keyConfig.id,
                status = if (success) ApiKeyStatus.ACTIVE else ApiKeyStatus.INVALID
            )
        }
    }

    fun selectActiveProviderAndModel(providerId: String, modelId: String) {
        viewModelScope.launch {
            apiKeyRepository.setActiveProviderAndModel(providerId, modelId)
        }
    }
}
