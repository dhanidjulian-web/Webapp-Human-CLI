package com.agon.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.model.ApiKeyConfig
import com.agon.app.data.model.ApiKeyStatus
import com.agon.app.data.model.ProviderSpec
import com.agon.app.ui.components.ApiKeyDialog
import com.agon.app.viewmodel.ProviderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderCatalogScreen(
    viewModel: ProviderViewModel,
    onNavigateToChat: () -> Unit
) {
    val context = LocalContext.current

    val filteredProviders by viewModel.filteredProviders.collectAsState()
    val apiKeys by viewModel.apiKeys.collectAsState()
    val activeProviderId by viewModel.activeProviderId.collectAsState()
    val activeModelId by viewModel.activeModelId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val testingKeyId by viewModel.testingKeyId.collectAsState()

    var showAddKeyDialogForProvider by remember { mutableStateOf<ProviderSpec?>(null) }

    val categories = listOf("Semua", "Gratis", "Freemium", "Lokal")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Spec Provider & BYOK",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daftar Provider Free/Freemium & Multi API Key Manager",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Category Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Cari provider (Groq, Gemini, DeepSeek...)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.onCategoryChange(cat) },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Provider Cards List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProviders, key = { it.id }) { provider ->
                    val providerKeys = apiKeys.filter { it.providerId == provider.id }
                    val isActiveProvider = (activeProviderId == provider.id)

                    ProviderCardItem(
                        provider = provider,
                        providerKeys = providerKeys,
                        isActiveProvider = isActiveProvider,
                        activeModelId = activeModelId,
                        testingKeyId = testingKeyId,
                        onSelectModel = { model ->
                            viewModel.selectActiveProviderAndModel(provider.id, model)
                            onNavigateToChat()
                        },
                        onAddKeyClick = { showAddKeyDialogForProvider = provider },
                        onDeleteKey = { keyId -> viewModel.deleteApiKey(keyId) },
                        onSetActiveKey = { keyId -> viewModel.setActiveKey(provider.id, keyId) },
                        onTestKey = { keyConfig -> viewModel.testKeyConnection(keyConfig) },
                        onOpenKeyUrl = { url ->
                            if (url.isNotBlank()) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                    )
                }
            }
        }
    }

    // Add API Key Dialog
    showAddKeyDialogForProvider?.let { provider ->
        ApiKeyDialog(
            provider = provider,
            onDismiss = { showAddKeyDialogForProvider = null },
            onSaveKey = { apiKey, label, endpointUrl ->
                viewModel.saveApiKey(provider.id, apiKey, label, endpointUrl)
                showAddKeyDialogForProvider = null
            }
        )
    }
}

@Composable
fun ProviderCardItem(
    provider: ProviderSpec,
    providerKeys: List<ApiKeyConfig>,
    isActiveProvider: Boolean,
    activeModelId: String,
    testingKeyId: String?,
    onSelectModel: (String) -> Unit,
    onAddKeyClick: () -> Unit,
    onDeleteKey: (String) -> Unit,
    onSetActiveKey: (String) -> Unit,
    onTestKey: (ApiKeyConfig) -> Unit,
    onOpenKeyUrl: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActiveProvider)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (isActiveProvider)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = provider.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (isActiveProvider) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AKTIF",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = provider.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Table Grid
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecTag(label = "Kecepatan", value = "${provider.speedScore.toInt()} tok/s")
                        SpecTag(label = "Skor Kode", value = "${provider.codeScore}/100")
                        SpecTag(label = "Konteks", value = provider.contextWindow)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🎁 Free Tier Spec: ${provider.freeTierDetails}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Available Models Selector Chips
            Text(
                text = "Pilih Model Pemrograman:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(provider.availableModels) { model ->
                    val isSelectedModel = isActiveProvider && (activeModelId == model)
                    Surface(
                        onClick = { onSelectModel(model) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelectedModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelectedModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = model,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = if (isSelectedModel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelectedModel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BYOK Multi-Key Section
            if (provider.isBYOKSupported) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔑 BYOK Keys (${providerKeys.size} Tersimpan):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        if (provider.apiKeyUrl.isNotBlank()) {
                            IconButton(
                                onClick = { onOpenKeyUrl(provider.apiKeyUrl) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Dapatkan API Key Gratis",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onAddKeyClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah API Key",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (providerKeys.isEmpty()) {
                    Text(
                        text = "Belum ada API Key khusus tersimpan. Aplikasi akan menggunakan fallback AI.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        providerKeys.forEach { keyConfig ->
                            val isTestingThisKey = testingKeyId == keyConfig.id

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (keyConfig.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Active Key Radio Check
                                        IconButton(
                                            onClick = { onSetActiveKey(keyConfig.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (keyConfig.isActive) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                                                contentDescription = "Set Active Key",
                                                tint = if (keyConfig.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Column {
                                            Text(
                                                text = keyConfig.label,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Key: ****${keyConfig.apiKey.takeLast(4)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Key Status Badge & Actions
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StatusBadge(status = keyConfig.status)

                                        Spacer(modifier = Modifier.width(4.dp))

                                        IconButton(
                                            onClick = { onTestKey(keyConfig) },
                                            enabled = !isTestingThisKey,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            if (isTestingThisKey) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Test Key Connection",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteKey(keyConfig.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Key",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: ApiKeyStatus) {
    val (bgColor, textColor, text) = when (status) {
        ApiKeyStatus.ACTIVE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "🟢 Aktif")
        ApiKeyStatus.INVALID -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "🔴 Invalid")
        ApiKeyStatus.QUOTA_EXCEEDED -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "🟠 Quota Full")
        ApiKeyStatus.UNTESTED -> Triple(Color(0xFFECEFF1), Color(0xFF455A64), "⚪ Belum Dites")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SpecTag(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
