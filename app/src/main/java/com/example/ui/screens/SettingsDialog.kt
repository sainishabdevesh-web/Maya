package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MayaPersona
import com.example.ui.theme.MayaBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaMagenta
import com.example.ui.theme.MayaPurple
import com.example.ui.theme.MayaPurpleDark
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun SettingsDialog(
    selectedPersona: MayaPersona,
    autoSpeak: Boolean,
    handsFreeMode: Boolean,
    speechPitch: Float,
    speechRate: Float,
    onSelectPersona: (MayaPersona) -> Unit,
    onToggleAutoSpeak: (Boolean) -> Unit,
    onToggleHandsFree: (Boolean) -> Unit,
    onChangePitch: (Float) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onClearChat: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MayaPurple.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = MayaDarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MayaMagenta.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MayaMagenta,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "MAYA Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MayaTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = MayaBorder, thickness = 1.dp)

                // Persona Selector
                Text(
                    text = "AI PERSONA & VOICE IDENTITY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MayaCyan,
                    letterSpacing = 1.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MayaPersona.values().forEach { persona ->
                        val isSelected = persona == selectedPersona
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MayaCardSurface else Color.Transparent)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MayaCyan else MayaBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectPersona(persona) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Psychology else Icons.Default.SmartToy,
                                contentDescription = persona.title,
                                tint = if (isSelected) MayaCyan else MayaTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = persona.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MayaTextPrimary else MayaTextSecondary
                                )
                                Text(
                                    text = persona.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MayaTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MayaBorder, thickness = 1.dp)

                // Voice Tuning Controls
                Text(
                    text = "FEMALE VOICE PARAMETERS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MayaMagenta,
                    letterSpacing = 1.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Vocal Pitch (Female Melodic Tone)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaTextSecondary
                        )
                        Text(
                            text = String.format("%.2fx", speechPitch),
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaMagenta,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = speechPitch,
                        onValueChange = onChangePitch,
                        valueRange = 0.8f..1.5f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = MayaMagenta,
                            activeTrackColor = MayaMagenta,
                            inactiveTrackColor = MayaBorder
                        ),
                        modifier = Modifier.testTag("pitch_slider")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speech Rate (Cadence)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaTextSecondary
                        )
                        Text(
                            text = String.format("%.2fx", speechRate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = onChangeSpeed,
                        valueRange = 0.7f..1.4f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = MayaCyan,
                            activeTrackColor = MayaCyan,
                            inactiveTrackColor = MayaBorder
                        ),
                        modifier = Modifier.testTag("speed_slider")
                    )
                }

                HorizontalDivider(color = MayaBorder, thickness = 1.dp)

                // Voice Behavior Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Speak Answers",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Text(
                            text = "MAYA vocalizes all knowledge & command outputs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = autoSpeak,
                        onCheckedChange = onToggleAutoSpeak,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MayaCyan,
                            checkedTrackColor = MayaPurpleDark
                        ),
                        modifier = Modifier.testTag("auto_speak_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hands-Free Voice Flow",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Text(
                            text = "Continuous listening cycle after speech completes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MayaTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = handsFreeMode,
                        onCheckedChange = onToggleHandsFree,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MayaMagenta,
                            checkedTrackColor = MayaPurpleDark
                        ),
                        modifier = Modifier.testTag("hands_free_switch")
                    )
                }

                HorizontalDivider(color = MayaBorder, thickness = 1.dp)

                // Actions & Info
                Button(
                    onClick = {
                        onClearChat()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_chat_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MayaCardSurface,
                        contentColor = MayaTextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset Conversation History")
                }
            }
        }
    }
}
