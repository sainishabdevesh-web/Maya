package com.example.data.model

import java.util.UUID

enum class MayaVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

enum class MayaPersona(val title: String, val description: String, val greeting: String) {
    EMPATHETIC_FRIENDLY(
        title = "Friendly & Warm",
        description = "Empathetic, cheerful, supportive female companion with high EQ and intellect",
        greeting = "Hello! I'm Maya, your personal AI. How can I brighten your day or assist you right now?"
    ),
    CRISP_JARVIS(
        title = "Jarvis Protocol",
        description = "Tactical, concise, ultra-polite futuristic mobile assistant",
        greeting = "MAYA systems operational. All mobile diagnostics normal. At your command."
    ),
    HIGH_KNOWLEDGE_SCHOLAR(
        title = "Omniscient Scholar",
        description = "Deep intellectual insights, science, philosophy, and advanced problem-solving",
        greeting = "Greetings. I am Maya, connected to global knowledge. What scientific, creative, or practical inquiry shall we explore?"
    )
}

enum class DeviceActionType {
    FLASHLIGHT_ON,
    FLASHLIGHT_OFF,
    BATTERY_STATUS,
    VOLUME_ADJUST,
    OPEN_APP,
    SET_TIMER,
    SET_ALARM,
    SEARCH_WEB,
    OPEN_MAPS,
    SYSTEM_BRIEFING,
    GENERAL_QUERY
}

data class DeviceActionResult(
    val type: DeviceActionType,
    val success: Boolean,
    val spokenResponse: String,
    val displayText: String,
    val extraData: String? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: DeviceActionType? = null,
    val actionTag: String? = null
)
