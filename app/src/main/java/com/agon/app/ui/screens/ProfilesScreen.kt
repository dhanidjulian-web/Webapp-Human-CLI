package com.agon.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.model.ModelMode
import com.agon.app.di.AppContainer
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.SectionHeader
import com.agon.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(container: AppContainer) {
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(container))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showAgentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.message, state.error) {
        state.message?.let {
            snackbar.showSnackbar(it)
            vm.clearMessage()
        }
        state.error?.let {
            snackbar.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Profiles & Agents", fontWeight = FontWeight.Bold)
                        Text(
                            "Room DB · max ${state.maxAgents} agents / profile",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAgentDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add agent")
            }
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
                    title = "Profiles",
                    action = {
                        TextButton(onClick = { showProfileDialog = true }) {
                            Text("New profile")
                        }
                    },
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.profiles.forEach { p ->
                        FilterChip(
                            selected = state.selectedProfileId == p.id,
                            onClick = { vm.selectProfile(p.id) },
                            label = { Text(p.name) },
                        )
                    }
                }
            }

            item {
                val progress = if (state.maxAgents == 0) 0f else state.agentCount.toFloat() / state.maxAgents
                Column {
                    Text(
                        "Agents ${state.agentCount} / ${state.maxAgents}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                }
            }

            if (state.agents.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.SmartToy,
                        title = "No agents",
                        subtitle = "Add an agent with system prompt instructions for this profile.",
                    )
                }
            } else {
                items(state.agents, key = { it.id }) { agent ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(agent.name, fontWeight = FontWeight.SemiBold)
                                if (agent.description.isNotBlank()) {
                                    Text(
                                        agent.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                if (agent.promptInstructions.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        agent.promptInstructions,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 4,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                if (agent.selectedModelId.isNotBlank()) {
                                    Text(
                                        "Model: ${agent.selectedModelId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            IconButton(onClick = { vm.deleteAgent(agent.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showProfileDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var model by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("New profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(model, { model = it }, label = { Text("Default model id") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.createProfile(name, desc, ModelMode.CLOUD, model)
                        showProfileDialog = false
                    },
                    enabled = name.isNotBlank(),
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Cancel") }
            },
        )
    }

    if (showAgentDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var prompt by remember { mutableStateOf("") }
        var model by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAgentDialog = false },
            title = { Text("Add agent") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(prompt, { prompt = it }, label = { Text("Prompt instructions") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(model, { model = it }, label = { Text("Model id") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.addAgent(name, desc, prompt, model)
                        showAgentDialog = false
                    },
                    enabled = name.isNotBlank() && state.selectedProfileId != null,
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAgentDialog = false }) { Text("Cancel") }
            },
        )
    }
}
