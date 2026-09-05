package com.agon.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.di.AppContainer
import com.agon.app.ui.theme.MusGoAmber
import com.agon.app.ui.theme.MusGoBg
import com.agon.app.ui.theme.MusGoCyan
import com.agon.app.ui.theme.MusGoGreen
import com.agon.app.ui.theme.MusGoOnBgMuted
import com.agon.app.ui.theme.MusGoOrange
import com.agon.app.ui.theme.MusGoRed
import com.agon.app.ui.theme.MusGoSurface
import com.agon.app.viewmodel.TerminalLine
import com.agon.app.viewmodel.TerminalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    sessionId: Long,
    container: AppContainer,
    onBack: () -> Unit,
) {
    val vm: TerminalViewModel = viewModel(
        key = "terminal_$sessionId",
        factory = TerminalViewModel.Factory(sessionId, container),
    )
    val state by vm.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.lines.size) {
        if (state.lines.isNotEmpty()) {
            listState.animateScrollToItem(state.lines.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column {
                        Text("CLI Session #$sessionId", fontWeight = FontWeight.Bold)
                        Text(
                            state.session?.currentWorkingDirectory ?: "…",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.policyNote != null) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        state.policyNote!!,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MusGoBg),
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.lines.size) { idx ->
                        val line = state.lines[idx]
                        TerminalLineRow(line)
                    }
                }
            }

            Surface(
                color = MusGoSurface,
                tonalElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "$",
                        color = MusGoCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    OutlinedTextField(
                        value = state.input,
                        onValueChange = vm::onInputChange,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isRunning,
                        singleLine = true,
                        placeholder = {
                            Text("command…", fontFamily = FontFamily.Monospace)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { vm.submit() }),
                        shape = RoundedCornerShape(12.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    if (state.isRunning) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { vm.submit() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Run",
                                tint = MusGoCyan,
                            )
                        }
                    }
                }
            }
        }
    }

    state.pendingDangerous?.let { cmd ->
        AlertDialog(
            onDismissRequest = { vm.cancelDangerous() },
            title = { Text("Dangerous command") },
            text = {
                Column {
                    Text("Policy classified this command as DANGEROUS. Confirm execution?")
                    Spacer(Modifier.height(8.dp))
                    Text(cmd, fontFamily = FontFamily.Monospace, color = MusGoOrange)
                }
            },
            confirmButton = {
                Button(onClick = { vm.confirmDangerous() }) { Text("Run anyway") }
            },
            dismissButton = {
                TextButton(onClick = { vm.cancelDangerous() }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun TerminalLineRow(line: TerminalLine) {
    val color = when (line.kind) {
        TerminalLine.Kind.PROMPT -> MusGoCyan
        TerminalLine.Kind.STDOUT -> Color(0xFFD1D5DB)
        TerminalLine.Kind.STDERR -> MusGoRed
        TerminalLine.Kind.SYSTEM -> MusGoOnBgMuted
        TerminalLine.Kind.META -> MusGoAmber
    }
    val weight = if (line.kind == TerminalLine.Kind.PROMPT) FontWeight.SemiBold else FontWeight.Normal
    Text(
        text = line.text,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = weight,
        lineHeight = 16.sp,
    )
}
