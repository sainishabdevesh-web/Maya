package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MayaVoiceState
import com.example.ui.components.MayaOrbVisualizer
import com.example.ui.theme.MayaBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaMagenta
import com.example.ui.theme.MayaPurple
import com.example.ui.theme.MayaPurpleDark
import com.example.ui.theme.MayaSuccess
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun VoiceCoreTab(
    voiceState: MayaVoiceState,
    audioAmplitude: Float,
    liveTranscript: String,
    latestResponse: String,
    onOrbClick: () -> Unit,
    onMicClick: () -> Unit,
    onQuickCommand: (String) -> Unit,
    onReplayAudio: () -> Unit
) {
    val scrollState = rememberScrollState()

    val quickCommands = listOf(
        "🔦 Turn on Flashlight",
        "🔋 Battery Check",
        "📋 Daily Briefing",
        "⏰ Set 5 Min Timer",
        "🌌 Explain Quantum Physics",
        "💡 Give me an empowering thought",
        "📷 Open Camera",
        "🗺️ Navigate to Coffee",
        "🔇 Mute Audio"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Holographic Status Pill
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .border(1.dp, MayaBorder, RoundedCornerShape(30.dp))
                .testTag("maya_status_indicator"),
            color = MayaCardSurface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (voiceState) {
                                MayaVoiceState.LISTENING -> MayaCyan
                                MayaVoiceState.SPEAKING -> MayaMagenta
                                MayaVoiceState.THINKING -> MayaPurple
                                MayaVoiceState.ERROR -> Color.Red
                                MayaVoiceState.IDLE -> MayaSuccess
                            }
                        )
                )
                Text(
                    text = when (voiceState) {
                        MayaVoiceState.LISTENING -> "LISTENING • SPEAK NOW"
                        MayaVoiceState.THINKING -> "THINKING • GEMINI 3.5 FLASH"
                        MayaVoiceState.SPEAKING -> "MAYA VOCALIZING"
                        MayaVoiceState.ERROR -> "SYSTEM ALERT"
                        MayaVoiceState.IDLE -> "MAYA NEURAL CORE • READY"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Holographic Orb
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MayaOrbVisualizer(
                state = voiceState,
                audioAmplitude = audioAmplitude,
                size = 230.dp,
                onClick = onOrbClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // State Guidance Label
        Text(
            text = when (voiceState) {
                MayaVoiceState.LISTENING -> "Speak your command or knowledge query..."
                MayaVoiceState.THINKING -> "Synthesizing intelligence..."
                MayaVoiceState.SPEAKING -> "Tap orb or mic to interrupt"
                MayaVoiceState.ERROR -> "Tap orb to restart"
                MayaVoiceState.IDLE -> "Tap orb to speak with Maya"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MayaTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Transcript or Response Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(MayaCyan.copy(alpha = 0.4f), MayaMagenta.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .testTag("response_card"),
            colors = CardDefaults.cardColors(containerColor = MayaCardSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice EQ",
                            tint = MayaCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (voiceState == MayaVoiceState.LISTENING) "VOICE INPUT" else "MAYA INTELLIGENCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MayaCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    if (latestResponse.isNotEmpty()) {
                        IconButton(
                            onClick = onReplayAudio,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("replay_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Replay audio",
                                tint = MayaMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Text(
                    text = if (liveTranscript.isNotEmpty()) {
                        "\"$liveTranscript\""
                    } else if (latestResponse.isNotEmpty()) {
                        latestResponse
                    } else {
                        "Greetings! How may I assist you with your device or knowledge today?"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MayaTextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Suggestion Chips (Horizontal Scroll)
        Text(
            text = "QUICK VOICE COMMANDS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MayaTextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickCommands.forEach { cmd ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MayaBorder, RoundedCornerShape(16.dp))
                        .clickable { onQuickCommand(cmd.substringAfter(" ")) }
                        .testTag("quick_cmd_${cmd.take(8).trim()}"),
                    color = MayaDarkSurface
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.bodySmall,
                        color = MayaTextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Push-to-Talk Mic Floating Button
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (voiceState == MayaVoiceState.LISTENING) MayaMagenta else MayaCyan,
                            MayaPurpleDark
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = if (voiceState == MayaVoiceState.LISTENING) MayaMagenta else MayaCyan,
                    shape = CircleShape
                )
                .clickable { onMicClick() }
                .testTag("push_to_talk_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (voiceState == MayaVoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = "Microphone",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (voiceState == MayaVoiceState.LISTENING) "TAP TO STOP" else "TAP MIC TO SPEAK",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MayaTextMuted,
            letterSpacing = 1.5.sp
        )
    }
}
