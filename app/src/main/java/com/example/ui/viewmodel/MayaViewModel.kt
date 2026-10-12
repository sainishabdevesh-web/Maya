package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.DeviceActionType
import com.example.data.model.MayaPersona
import com.example.data.model.MayaVoiceState
import com.example.data.repository.MayaRepository
import com.example.device.DeviceCommandExecutor
import com.example.voice.MayaSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MayaUiState(
    val voiceState: MayaVoiceState = MayaVoiceState.IDLE,
    val audioAmplitude: Float = 0f,
    val liveTranscript: String = "",
    val latestResponse: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val persona: MayaPersona = MayaPersona.EMPATHETIC_FRIENDLY,
    val isTorchActive: Boolean = false,
    val batteryLevel: Int = 90,
    val isCharging: Boolean = false,
    val apiKey: String = "",
    val hasApiKey: Boolean = false,
    val autoSpeak: Boolean = true,
    val handsFreeMode: Boolean = false,
    val speechPitch: Float = 1.15f,
    val speechRate: Float = 1.0f,
    val activeTab: Int = 0, // 0 = Voice Core, 1 = Chat History, 2 = Device Deck
    val showSettingsDialog: Boolean = false,
    val showApiKeyDialog: Boolean = false,
    val toastMessage: String? = null
)

class MayaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MayaRepository(application)
    private val commandExecutor = DeviceCommandExecutor(application)

    private val _uiState = MutableStateFlow(MayaUiState())
    val uiState: StateFlow<MayaUiState> = _uiState.asStateFlow()

    private val speechManager = MayaSpeechManager(
        context = application,
        onSpeechRecognized = { text ->
            processInput(text)
        },
        onPartialTranscript = { partial ->
            _uiState.update { it.copy(liveTranscript = partial) }
        },
        onError = { errorMsg ->
            _uiState.update {
                it.copy(
                    voiceState = MayaVoiceState.IDLE,
                    liveTranscript = "",
                    toastMessage = errorMsg
                )
            }
        }
    )

    init {
        // Load initial settings
        val initialPersona = repository.getSelectedPersona()
        val apiKey = repository.getApiKey()
        val autoSpeak = repository.isAutoSpeakEnabled()
        val handsFree = repository.isContinuousModeEnabled()
        val pitch = repository.getTtsPitch()
        val speed = repository.getTtsSpeed()

        speechManager.speechPitch = pitch
        speechManager.speechRate = speed

        val (battery, charging) = commandExecutor.getBatteryInfo()

        val welcomeMsg = ChatMessage(
            text = initialPersona.greeting,
            isUser = false,
            actionTag = "MAYA INITIALIZED"
        )

        _uiState.update {
            it.copy(
                persona = initialPersona,
                apiKey = apiKey,
                hasApiKey = apiKey.isNotEmpty(),
                autoSpeak = autoSpeak,
                handsFreeMode = handsFree,
                speechPitch = pitch,
                speechRate = speed,
                batteryLevel = battery,
                isCharging = charging,
                isTorchActive = commandExecutor.isTorchActive(),
                messages = listOf(welcomeMsg),
                latestResponse = initialPersona.greeting
            )
        }

        // Collect audio amplitude for real-time visualizer
        viewModelScope.launch {
            speechManager.rmsAmplitude.collect { amp ->
                _uiState.update { it.copy(audioAmplitude = amp) }
            }
        }

        // Collect listening state
        viewModelScope.launch {
            speechManager.isListening.collect { listening ->
                if (listening) {
                    _uiState.update { it.copy(voiceState = MayaVoiceState.LISTENING) }
                } else if (_uiState.value.voiceState == MayaVoiceState.LISTENING) {
                    _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
                }
            }
        }

        // Collect speaking state
        viewModelScope.launch {
            speechManager.isSpeaking.collect { speaking ->
                if (speaking) {
                    _uiState.update { it.copy(voiceState = MayaVoiceState.SPEAKING) }
                } else if (_uiState.value.voiceState == MayaVoiceState.SPEAKING) {
                    _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
                    if (_uiState.value.handsFreeMode) {
                        speechManager.startListening()
                    }
                }
            }
        }
    }

    fun onOrbClicked() {
        when (_uiState.value.voiceState) {
            MayaVoiceState.LISTENING -> {
                speechManager.stopListening()
                _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
            }
            MayaVoiceState.SPEAKING -> {
                speechManager.stopSpeaking()
                _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
            }
            else -> {
                startVoiceListening()
            }
        }
    }

    fun startVoiceListening() {
        speechManager.stopSpeaking()
        _uiState.update { it.copy(liveTranscript = "Listening to your voice...") }
        speechManager.startListening()
    }

    fun stopVoiceListening() {
        speechManager.stopListening()
        _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE, liveTranscript = "") }
    }

    fun processInput(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        speechManager.stopListening()
        _uiState.update {
            it.copy(
                voiceState = MayaVoiceState.THINKING,
                liveTranscript = "",
                messages = it.messages + ChatMessage(text = trimmed, isUser = true)
            )
        }

        // 1. Check if it's a mobile device control command
        val deviceResult = commandExecutor.checkAndExecuteCommand(trimmed)
        if (deviceResult != null) {
            val responseMsg = ChatMessage(
                text = deviceResult.displayText,
                isUser = false,
                actionType = deviceResult.type,
                actionTag = deviceResult.type.name
            )

            val (bat, charg) = commandExecutor.getBatteryInfo()
            _uiState.update {
                it.copy(
                    messages = it.messages + responseMsg,
                    latestResponse = deviceResult.spokenResponse,
                    isTorchActive = commandExecutor.isTorchActive(),
                    batteryLevel = bat,
                    isCharging = charg
                )
            }

            if (_uiState.value.autoSpeak) {
                speechManager.speak(deviceResult.spokenResponse)
            } else {
                _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
            }
            return
        }

        // 2. High-Knowledge query via Gemini 3.5 Flash or Smart Fallback
        viewModelScope.launch {
            val history = _uiState.value.messages
            val persona = _uiState.value.persona
            val result = repository.queryMaya(trimmed, history, persona)

            result.onSuccess { reply ->
                val mayaMsg = ChatMessage(
                    text = reply,
                    isUser = false,
                    actionType = DeviceActionType.GENERAL_QUERY,
                    actionTag = "MAYA KNOWLEDGE"
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + mayaMsg,
                        latestResponse = reply
                    )
                }

                if (_uiState.value.autoSpeak) {
                    speechManager.speak(reply)
                } else {
                    _uiState.update { it.copy(voiceState = MayaVoiceState.IDLE) }
                }
            }.onFailure { error ->
                val errorMsg = "Core connection glitch: ${error.localizedMessage ?: "Unknown error"}"
                _uiState.update {
                    it.copy(
                        voiceState = MayaVoiceState.ERROR,
                        toastMessage = errorMsg,
                        messages = it.messages + ChatMessage(
                            text = "I encountered an error reaching the knowledge core. Please verify your free API key or network connection.",
                            isUser = false,
                            actionTag = "ERROR"
                        )
                    )
                }
            }
        }
    }

    fun toggleTorchDirect() {
        val newState = !_uiState.value.isTorchActive
        val success = commandExecutor.toggleTorch(newState)
        if (success) {
            _uiState.update { it.copy(isTorchActive = newState) }
            val confirmation = if (newState) "Flashlight illuminated." else "Flashlight turned off."
            if (_uiState.value.autoSpeak) {
                speechManager.speak(confirmation)
            }
        }
    }

    fun refreshBattery() {
        val (bat, charg) = commandExecutor.getBatteryInfo()
        _uiState.update { it.copy(batteryLevel = bat, isCharging = charg) }
        val msg = "Battery is at $bat% ${if (charg) "(charging)" else ""}."
        if (_uiState.value.autoSpeak) {
            speechManager.speak(msg)
        }
    }

    fun triggerQuickCommand(command: String) {
        processInput(command)
    }

    fun replayMessage(text: String) {
        speechManager.speak(text)
    }

    fun clearChat() {
        val welcome = ChatMessage(
            text = _uiState.value.persona.greeting,
            isUser = false,
            actionTag = "MAYA RESET"
        )
        _uiState.update { it.copy(messages = listOf(welcome), latestResponse = _uiState.value.persona.greeting) }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(activeTab = index) }
    }

    fun setPersona(persona: MayaPersona) {
        repository.savePersona(persona)
        _uiState.update { it.copy(persona = persona) }
        speechManager.speak("Persona updated to ${persona.title}. ${persona.greeting}")
    }

    fun saveApiKey(newKey: String) {
        repository.saveCustomApiKey(newKey)
        val hasKey = newKey.trim().isNotEmpty()
        _uiState.update {
            it.copy(
                apiKey = newKey.trim(),
                hasApiKey = hasKey,
                showApiKeyDialog = false,
                toastMessage = if (hasKey) "API key saved successfully!" else "Key cleared. Using default mode."
            )
        }
    }

    fun setAutoSpeak(enabled: Boolean) {
        repository.setAutoSpeakEnabled(enabled)
        _uiState.update { it.copy(autoSpeak = enabled) }
    }

    fun setHandsFreeMode(enabled: Boolean) {
        repository.setContinuousModeEnabled(enabled)
        _uiState.update { it.copy(handsFreeMode = enabled) }
        if (enabled && _uiState.value.voiceState == MayaVoiceState.IDLE) {
            startVoiceListening()
        }
    }

    fun setTtsPitch(pitch: Float) {
        repository.saveTtsPitch(pitch)
        speechManager.speechPitch = pitch
        _uiState.update { it.copy(speechPitch = pitch) }
    }

    fun setTtsSpeed(speed: Float) {
        repository.saveTtsSpeed(speed)
        speechManager.speechRate = speed
        _uiState.update { it.copy(speechRate = speed) }
    }

    fun showSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun showApiKeyDialog(show: Boolean) {
        _uiState.update { it.copy(showApiKeyDialog = show) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.shutdown()
    }
}
