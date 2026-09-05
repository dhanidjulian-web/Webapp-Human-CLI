package com.agon.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.agon.app.viewmodel.CallState
import com.agon.app.viewmodel.VoiceCallViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCallScreen(viewModel: VoiceCallViewModel) {
    val context = LocalContext.current

    val callState by viewModel.callState.collectAsState()
    val transcripts by viewModel.transcripts.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()
    val selectedPersona by viewModel.selectedPersona.collectAsState()
    val audioWaveLevel by viewModel.audioWaveLevel.collectAsState()
    val currentSpokenText by viewModel.currentSpokenText.collectAsState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.startCall()
        }
    }

    val personas = listOf("Senior Architect", "Code Reviewer", "Quick Debugger", "Friendly Mentor")

    // Pulsing animation transition for audio orb
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Voice Chat AI (Telp)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Percakapan Interaktif Real-time Bahasa Indonesia",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Persona Selector Row
            Text(
                text = "Pilih Karakter AI Assistant:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(personas) { persona ->
                    FilterChip(
                        selected = selectedPersona == persona,
                        onClick = { viewModel.selectPersona(persona) },
                        label = { Text(persona) },
                        enabled = callState == CallState.IDLE || callState == CallState.ENDED
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Animated Audio Orb & Call Status Visualizer
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(if (callState == CallState.SPEAKING || callState == CallState.LISTENING) pulseScale else 1.0f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = when (callState) {
                                CallState.SPEAKING -> listOf(Color(0xFF8E24AA), Color(0xFF3F51B5), Color(0xFF00BCD4))
                                CallState.LISTENING -> listOf(Color(0xFF4CAF50), Color(0xFF009688), Color(0xFF00BCD4))
                                CallState.THINKING -> listOf(Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFFE91E63))
                                CallState.CONNECTED -> listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                else -> listOf(Color.Gray, Color.DarkGray)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (callState) {
                        CallState.SPEAKING -> Icons.Default.RecordVoiceOver
                        CallState.LISTENING -> Icons.Default.GraphicEq
                        CallState.THINKING -> Icons.Default.GraphicEq
                        else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Call Status Label
            Text(
                text = when (callState) {
                    CallState.IDLE -> "Siap Memulai Panggilan Telepon AI"
                    CallState.CONNECTING -> "Menghubungkan Panggilan AI..."
                    CallState.CONNECTED -> "Terhubung dengan $selectedPersona"
                    CallState.LISTENING -> "🎤 Silakan Bicara (Mendengarkan...)"
                    CallState.THINKING -> "🤔 KodeAI sedang memikirkan jawaban..."
                    CallState.SPEAKING -> "🔊 KodeAI sedang berbicara..."
                    CallState.ENDED -> "Panggilan Selesai"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = when (callState) {
                    CallState.SPEAKING -> MaterialTheme.colorScheme.primary
                    CallState.LISTENING -> Color(0xFF2E7D32)
                    CallState.THINKING -> Color(0xFFE65100)
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )

            if (currentSpokenText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"$currentSpokenText\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Call Control Buttons (Start / Speech Trigger / Mute / Speaker / End)
            if (callState == CallState.IDLE || callState == CallState.ENDED) {
                Button(
                    onClick = {
                        if (hasMicPermission) {
                            viewModel.startCall()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MULAI TELEPON AI", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = { viewModel.toggleMute() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Push to Speak Button
                    Button(
                        onClick = { viewModel.listenToUserSpeech() },
                        enabled = callState != CallState.THINKING && callState != CallState.SPEAKING,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Bicara",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Speakerphone
                    IconButton(
                        onClick = { viewModel.toggleSpeaker() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Speaker",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // End Call
                    IconButton(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Akhiri Panggilan",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live Transcript History
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "📜 Live Transcript Percakapan:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (transcripts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Transkrip percakapan telepon akan muncul di sini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(transcripts, key = { it.id }) { item ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (item.speaker == "Anda")
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = item.speaker,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
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
