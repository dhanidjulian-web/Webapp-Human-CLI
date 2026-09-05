package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.viewmodel.AlpineSandboxViewModel
import com.agon.app.viewmodel.TerminalLineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen(viewModel: AlpineSandboxViewModel) {
    val outputLines by viewModel.terminalOutput.collectAsState()
    val currentCmd by viewModel.currentCommand.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(outputLines.size) {
        if (outputLines.isNotEmpty()) {
            listState.animateScrollToItem(outputLines.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF28C840)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sandbox Alpine Linux (Kai)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "SimonSchubert/Kai - Isolated Linux Container",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = Color(0xFF8B949E)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.executeCommand("clear") }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Console",
                            tint = Color(0xFFFF7B72)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF161B22)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0D1117)) // Dark Terminal Background
        ) {
            // Traffic Light Window Header (Matching SVG Input File style)
            Surface(
                color = Color(0xFF161B22),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F57)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF28C840)))
                    }
                    Text(
                        text = "bash — alpine@kai-container",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF8B949E)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF238636)
                    ) {
                        Text(
                            text = "CLI READY",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Preset Action Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(Color(0xFF161B22))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickCommandChip(label = "neofetch", onClick = { viewModel.executeCommand("neofetch") })
                QuickCommandChip(label = "apk add python3", onClick = { viewModel.executeCommand("apk add python3") })
                QuickCommandChip(label = "apk add nodejs", onClick = { viewModel.executeCommand("apk add nodejs") })
                QuickCommandChip(label = "git clone Kai", onClick = { viewModel.executeCommand("git clone https://github.com/SimonSchubert/Kai.git") })
                QuickCommandChip(label = "help", onClick = { viewModel.executeCommand("help") })
            }

            // Terminal Output Console
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(outputLines, key = { it.id }) { line ->
                    val color = when (line.type) {
                        TerminalLineType.PROMPT -> Color(0xFF58A6FF)
                        TerminalLineType.SUCCESS -> Color(0xFF3FB950)
                        TerminalLineType.ERROR -> Color(0xFFFF7B72)
                        TerminalLineType.SYSTEM -> Color(0xFF8B949E)
                        TerminalLineType.OUTPUT -> Color(0xFFC9D1D9)
                    }

                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = color
                    )
                }

                if (isExecuting) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF3FB950),
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Executing in Alpine Kai Sandbox...",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF8B949E)
                            )
                        }
                    }
                }
            }

            // Command Prompt Input Field
            Surface(
                color = Color(0xFF161B22),
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "➜  ~ $ ",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3FB950),
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = currentCmd,
                        onValueChange = { viewModel.onCommandChange(it) },
                        placeholder = {
                            Text(
                                text = "apk add python3, neofetch, node...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFF484F5E)
                            )
                        },
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color(0xFFE6EDF3)
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF58A6FF),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedContainerColor = Color(0xFF0D1117),
                            unfocusedContainerColor = Color(0xFF0D1117)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.executeCommand() })
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.executeCommand() },
                        enabled = currentCmd.isNotBlank(),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF238636))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run Command",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickCommandChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF21262D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF58A6FF),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
