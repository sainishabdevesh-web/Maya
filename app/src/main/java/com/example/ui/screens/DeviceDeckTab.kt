package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class QuickAppAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val action: (Context) -> Unit
)

@Composable
fun DeviceDeckTab(
    isTorchActive: Boolean,
    batteryLevel: Int,
    isCharging: Boolean,
    onToggleTorch: () -> Unit,
    onRefreshBattery: () -> Unit,
    onTriggerVoiceCommand: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val quickApps = listOf(
        QuickAppAction("Camera", Icons.Default.CameraAlt, MayaCyan) { ctx ->
            safeLaunch(ctx, Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        },
        QuickAppAction("YouTube", Icons.Default.PlayArrow, Color(0xFFFF0000)) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")))
        },
        QuickAppAction("Maps", Icons.Default.Map, Color(0xFF34A853)) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=restaurants")))
        },
        QuickAppAction("Alarms", Icons.Default.Alarm, Color(0xFFFBBC05)) { ctx ->
            safeLaunch(ctx, Intent(AlarmClock.ACTION_SHOW_ALARMS))
        },
        QuickAppAction("Calendar", Icons.Default.CalendarMonth, Color(0xFF4285F4)) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALENDAR) })
        },
        QuickAppAction("Browser", Icons.Default.Language, MayaPurple) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
        },
        QuickAppAction("Dialer", Icons.Default.Call, MayaSuccess) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_DIAL))
        },
        QuickAppAction("Messages", Icons.AutoMirrored.Filled.Message, MayaMagenta) { ctx ->
            safeLaunch(ctx, Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MESSAGING) })
        },
        QuickAppAction("Wi-Fi", Icons.Default.Wifi, MayaCyan) { ctx ->
            safeLaunch(ctx, Intent(Settings.ACTION_WIFI_SETTINGS))
        },
        QuickAppAction("Settings", Icons.Default.Settings, MayaTextSecondary) { ctx ->
            safeLaunch(ctx, Intent(Settings.ACTION_SETTINGS))
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Section: Tactical Telemetry Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Flashlight Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = if (isTorchActive) MayaCyan else MayaBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onToggleTorch() }
                    .testTag("torch_toggle_card"),
                colors = CardDefaults.cardColors(containerColor = MayaCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isTorchActive) MayaCyan else MayaDarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isTorchActive) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = if (isTorchActive) Color.Black else MayaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "FLASHLIGHT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isTorchActive) "ILLUMINATED" else "OFF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isTorchActive) MayaCyan else MayaTextSecondary
                    )
                }
            }

            // Battery Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, MayaBorder, RoundedCornerShape(18.dp))
                    .testTag("battery_status_card"),
                colors = CardDefaults.cardColors(containerColor = MayaCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isCharging) MayaSuccess.copy(alpha = 0.2f) else MayaDarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                contentDescription = "Battery",
                                tint = if (isCharging) MayaSuccess else MayaCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onRefreshBattery,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Battery",
                                tint = MayaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "POWER CORE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$batteryLevel% ${if (isCharging) "(Charging)" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                }
            }
        }

        // Daily Briefing Action Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, MayaMagenta.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .clickable { onTriggerVoiceCommand("Give me a full daily system briefing") }
                .testTag("daily_briefing_card"),
            color = MayaCardSurface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MAYA JARVIS BRIEFING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MayaMagenta,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Vocalize date, time, battery & intelligence status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MayaTextSecondary
                    )
                }
                Button(
                    onClick = { onTriggerVoiceCommand("Give me a full daily system briefing") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MayaMagenta,
                        contentColor = Color.White
                    )
                ) {
                    Text("Briefing", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Audio Volume Tactical Row
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, MayaBorder, RoundedCornerShape(18.dp)),
            color = MayaCardSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AUDIO VOLUME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Direct Media Control",
                        style = MaterialTheme.typography.bodySmall,
                        color = MayaTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onTriggerVoiceCommand("Turn down volume") },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MayaDarkSurface)
                            .testTag("volume_down_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                            contentDescription = "Volume Down",
                            tint = MayaCyan
                        )
                    }

                    IconButton(
                        onClick = { onTriggerVoiceCommand("Mute audio") },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MayaDarkSurface)
                            .testTag("volume_mute_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeMute,
                            contentDescription = "Mute",
                            tint = MayaMagenta
                        )
                    }

                    IconButton(
                        onClick = { onTriggerVoiceCommand("Turn up volume") },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MayaDarkSurface)
                            .testTag("volume_up_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Volume Up",
                            tint = MayaCyan
                        )
                    }
                }
            }
        }

        // Quick Launch Hub
        Text(
            text = "MOBILE LAUNCH PAD",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MayaTextMuted,
            letterSpacing = 1.sp
        )

        // 2-row App Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val chunked = quickApps.chunked(5)
            chunked.forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowApps.forEach { appItem ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { appItem.action(context) }
                                .padding(vertical = 6.dp)
                                .testTag("app_${appItem.title.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MayaCardSurface)
                                    .border(1.dp, MayaBorder, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = appItem.icon,
                                    contentDescription = appItem.title,
                                    tint = appItem.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = appItem.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MayaTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun safeLaunch(context: Context, intent: Intent) {
    try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (_: Exception) {}
}
