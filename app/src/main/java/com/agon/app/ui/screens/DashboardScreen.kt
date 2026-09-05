package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.di.AppContainer
import com.agon.app.ui.components.MonoText
import com.agon.app.ui.components.SectionHeader
import com.agon.app.ui.components.StatCard
import com.agon.app.ui.components.StatusChip
import com.agon.app.ui.theme.MusGoAmber
import com.agon.app.ui.theme.MusGoBlue
import com.agon.app.ui.theme.MusGoCyan
import com.agon.app.ui.theme.MusGoGreen
import com.agon.app.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(container: AppContainer) {
    val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(container))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "MusGo-OS", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Fondasi Backend · Fase 01",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        title = "Sandboxes",
                        value = state.sandboxCount.toString(),
                        icon = Icons.Default.Dns,
                        accent = MusGoCyan,
                        cardModifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "Running",
                        value = state.runningCount.toString(),
                        icon = Icons.Default.Terminal,
                        accent = MusGoGreen,
                        cardModifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        title = "Vault Keys",
                        value = state.credentialCount.toString(),
                        icon = Icons.Default.Key,
                        accent = MusGoAmber,
                        cardModifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "Agents",
                        value = state.agentCount.toString(),
                        icon = Icons.Default.SmartToy,
                        accent = MusGoBlue,
                        cardModifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Sandbox Fleet",
                    subtitle = "Local process isolation under app-private cache",
                )
            }

            if (state.sandboxes.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = "No sandboxes yet. Open the Sandbox tab to create one.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            } else {
                items(items = state.sandboxes.take(5), key = { it.id }) { sb ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Column(modifier = Modifier.padding(all = 14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = sb.name, fontWeight = FontWeight.SemiBold)
                                StatusChip(status = sb.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            MonoText(
                                text = "${sb.linuxDistro.displayName} · ${sb.storagePath}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                            Text(
                                text = "Sessions: ${sb.activeSessionCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Recent Executions",
                    subtitle = "Persisted command history with exit codes and log paths",
                )
            }

            if (state.recentCommands.isEmpty()) {
                item {
                    Text(
                        text = "No commands executed yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                items(items = state.recentCommands, key = { it.id }) { cmd ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(modifier = Modifier.padding(all = 12.dp)) {
                            MonoText(text = "$ ${cmd.commandText}", maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${cmd.status} · exit ${cmd.exitCode ?: "—"} · ${cmd.durationMs}ms · ${timeFmt.format(Date(cmd.timestamp))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Secure Vault Snapshot")
            }

            if (state.credentials.isEmpty()) {
                item {
                    Text(
                        text = "Vault empty — secrets stored AES-256-GCM via Android Keystore.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                items(items = state.credentials.take(5), key = { it.id }) { cred ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = cred.label, fontWeight = FontWeight.Medium)
                                Text(
                                    text = cred.type.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            MonoText(text = cred.redactedValue, color = MusGoCyan)
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${state.profileCount} profile(s) · max 30 agents each",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
