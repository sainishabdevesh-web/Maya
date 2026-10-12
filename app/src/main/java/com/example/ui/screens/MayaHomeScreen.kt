package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.MayaBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaMagenta
import com.example.ui.theme.MayaPurple
import com.example.ui.theme.MayaSpaceBlack
import com.example.ui.theme.MayaSuccess
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import com.example.ui.viewmodel.MayaViewModel

@Composable
fun MayaHomeScreen(viewModel: MayaViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceListening()
        } else {
            Toast.makeText(
                context,
                "Microphone permission is required for voice control.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun checkAndRequestMic(action: () -> Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            action()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MayaSpaceBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Futuristic Top Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars),
                color = MayaDarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MayaCyan.copy(alpha = 0.15f))
                                .border(1.dp, MayaCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "MAYA Icon",
                                tint = MayaCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "MAYA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MayaTextPrimary,
                                    letterSpacing = 2.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MayaMagenta)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AI",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "Voice Assistant • Gemini 3.5",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MayaTextMuted
                            )
                        }
                    }

                    // Top Action Icons: Free API Key & Settings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Free API Key Pill/Button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    if (uiState.hasApiKey) MayaSuccess.copy(alpha = 0.6f) else MayaCyan.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.showApiKeyDialog(true) }
                                .testTag("top_api_key_button"),
                            color = MayaCardSurface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.hasApiKey) Icons.Default.Key else Icons.Default.KeyOff,
                                    contentDescription = "API Key",
                                    tint = if (uiState.hasApiKey) MayaSuccess else MayaCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (uiState.hasApiKey) "FREE KEY" else "GET KEY",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.hasApiKey) MayaSuccess else MayaCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Settings Button
                        IconButton(
                            onClick = { viewModel.showSettingsDialog(true) },
                            modifier = Modifier.testTag("top_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MayaTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation with Insets
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MayaDarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice Core",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Voice Core", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MayaCyan,
                        selectedTextColor = MayaCyan,
                        unselectedIconColor = MayaTextMuted,
                        unselectedTextColor = MayaTextMuted,
                        indicatorColor = MayaCyan.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_voice")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "Chat Feed",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Chat Feed", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MayaMagenta,
                        selectedTextColor = MayaMagenta,
                        unselectedIconColor = MayaTextMuted,
                        unselectedTextColor = MayaTextMuted,
                        indicatorColor = MayaMagenta.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_chat")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Device Deck",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Device Deck", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MayaPurple,
                        selectedTextColor = MayaPurple,
                        unselectedIconColor = MayaTextMuted,
                        unselectedTextColor = MayaTextMuted,
                        indicatorColor = MayaPurple.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_deck")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> VoiceCoreTab(
                        voiceState = uiState.voiceState,
                        audioAmplitude = uiState.audioAmplitude,
                        liveTranscript = uiState.liveTranscript,
                        latestResponse = uiState.latestResponse,
                        onOrbClick = { checkAndRequestMic { viewModel.onOrbClicked() } },
                        onMicClick = { checkAndRequestMic { viewModel.startVoiceListening() } },
                        onQuickCommand = { cmd -> viewModel.triggerQuickCommand(cmd) },
                        onReplayAudio = {
                            if (uiState.latestResponse.isNotEmpty()) {
                                viewModel.replayMessage(uiState.latestResponse)
                            }
                        }
                    )
                    1 -> ChatStreamTab(
                        messages = uiState.messages,
                        onSendMessage = { text -> viewModel.processInput(text) },
                        onMicClick = { checkAndRequestMic { viewModel.startVoiceListening() } },
                        onReplayAudio = { text -> viewModel.replayMessage(text) }
                    )
                    2 -> DeviceDeckTab(
                        isTorchActive = uiState.isTorchActive,
                        batteryLevel = uiState.batteryLevel,
                        isCharging = uiState.isCharging,
                        onToggleTorch = { viewModel.toggleTorchDirect() },
                        onRefreshBattery = { viewModel.refreshBattery() },
                        onTriggerVoiceCommand = { cmd -> viewModel.triggerQuickCommand(cmd) }
                    )
                }
            }

            // Dialogs
            if (uiState.showApiKeyDialog) {
                ApiKeyDialog(
                    currentKey = uiState.apiKey,
                    onSaveKey = { newKey -> viewModel.saveApiKey(newKey) },
                    onDismiss = { viewModel.showApiKeyDialog(false) }
                )
            }

            if (uiState.showSettingsDialog) {
                SettingsDialog(
                    selectedPersona = uiState.persona,
                    autoSpeak = uiState.autoSpeak,
                    handsFreeMode = uiState.handsFreeMode,
                    speechPitch = uiState.speechPitch,
                    speechRate = uiState.speechRate,
                    onSelectPersona = { p -> viewModel.setPersona(p) },
                    onToggleAutoSpeak = { enabled -> viewModel.setAutoSpeak(enabled) },
                    onToggleHandsFree = { enabled -> viewModel.setHandsFreeMode(enabled) },
                    onChangePitch = { pitch -> viewModel.setTtsPitch(pitch) },
                    onChangeSpeed = { speed -> viewModel.setTtsSpeed(speed) },
                    onClearChat = { viewModel.clearChat() },
                    onDismiss = { viewModel.showSettingsDialog(false) }
                )
            }
        }
    }
}
