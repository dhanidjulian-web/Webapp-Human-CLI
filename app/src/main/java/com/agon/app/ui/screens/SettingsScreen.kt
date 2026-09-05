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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.MusGoApp
import com.agon.app.ui.components.MonoText
import com.agon.app.ui.components.SectionHeader
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as MusGoApp
    val sandboxRoot = remember {
        File(context.cacheDir, "sandbox").absolutePath
    }
    val dbPath = remember {
        context.getDatabasePath("musgo_os.db").absolutePath
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("System", fontWeight = FontWeight.Bold)
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SectionHeader(
                    title = "MusGo-OS 2in1",
                    subtitle = "Fase 01 — Fondasi Backend, Secure Vault, Sandbox & CLI",
                )
            }

            item {
                InfoCard(
                    icon = Icons.Default.Lock,
                    title = "Secure Credential Vault",
                    body = "AES-256-GCM via Android Keystore alias musgo_os_credential_master_v1. " +
                        "Plaintext never written to Room or logs. UI receives redacted previews only " +
                        "(ghp_•••• / sk-••••). Live connectivity tests for GitHub, OpenRouter, Gemini, Groq, OpenAI, Anthropic.",
                )
            }
            item {
                InfoCard(
                    icon = Icons.Default.Storage,
                    title = "Room Persistent Database",
                    body = "Tables: profiles, agents, sandboxes, terminal_sessions, command_executions, " +
                        "command_policies, credentials. Reactive Kotlin Flow DAOs. Max 30 agents per profile.",
                )
                Spacer(Modifier.height(6.dp))
                MonoText(dbPath, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                InfoCard(
                    icon = Icons.Default.Terminal,
                    title = "Sandbox & Process Executor",
                    body = "LocalProcessExecutor (ProcessBuilder) with 30s hard timeout, async stdout/stderr " +
                        "capture to private log files, path jail under app cache/sandbox. " +
                        "Lifecycle: create → initialize → executeCommand → stop → destroy (exports preserved).",
                )
                Spacer(Modifier.height(6.dp))
                MonoText(sandboxRoot, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                InfoCard(
                    icon = Icons.Default.Security,
                    title = "Command Policy Engine",
                    body = "Regex-based classification: SAFE, REVIEW_REQUIRED, DANGEROUS, BLOCKED. " +
                        "Blocked commands never spawn a process; dangerous commands require explicit CLI confirm.",
                )
            }
            item {
                InfoCard(
                    icon = Icons.Default.Info,
                    title = "Architecture",
                    body = "MVVM + Clean layers · AppContainer DI · Keystore crypto · OkHttp validators · " +
                        "Room 2.8 + KSP · Compose Material 3",
                )
            }

            item {
                Text(
                    "Backend container ready: ${app.container.javaClass.simpleName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    body: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp, top = 2.dp),
            )
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
