package com.example.device

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import com.example.data.model.DeviceActionResult
import com.example.data.model.DeviceActionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceCommandExecutor(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var isTorchOn: Boolean = false

    fun isTorchActive(): Boolean = isTorchOn

    fun checkAndExecuteCommand(query: String): DeviceActionResult? {
        val lower = query.trim().lowercase(Locale.ROOT)

        // 1. Flashlight / Torch
        if (lower.contains("flashlight") || lower.contains("torch") || lower == "lumos" || lower == "nox") {
            val turnOn = lower.contains("on") || lower == "lumos" || (!lower.contains("off") && !isTorchOn)
            val success = toggleTorch(turnOn)
            return if (success) {
                DeviceActionResult(
                    type = if (turnOn) DeviceActionType.FLASHLIGHT_ON else DeviceActionType.FLASHLIGHT_OFF,
                    success = true,
                    spokenResponse = if (turnOn) "Illuminating flashlight now, Sir." else "Flashlight turned off.",
                    displayText = if (turnOn) "Flashlight Activated 🔦" else "Flashlight Deactivated"
                )
            } else {
                DeviceActionResult(
                    type = DeviceActionType.FLASHLIGHT_ON,
                    success = false,
                    spokenResponse = "I was unable to access the camera flash on this device.",
                    displayText = "Flashlight not available on device"
                )
            }
        }

        // 2. Battery Status
        if (lower.contains("battery") || lower.contains("charge") || lower.contains("power level")) {
            val (level, isCharging) = getBatteryInfo()
            val chargeText = if (isCharging) "and is currently charging" else "and not connected to power"
            return DeviceActionResult(
                type = DeviceActionType.BATTERY_STATUS,
                success = true,
                spokenResponse = "Power diagnostics complete. Battery level is at $level percent $chargeText.",
                displayText = "Battery: $level% ${if (isCharging) "⚡ Charging" else "🔋"}",
                extraData = "$level%"
            )
        }

        // 3. System Briefing / Status
        if (lower.contains("briefing") || lower.contains("system status") || lower == "status report" || lower.contains("daily brief")) {
            val (level, isCharging) = getBatteryInfo()
            val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val now = Date()
            val dateStr = dateFormat.format(now)
            val timeStr = timeFormat.format(now)
            val speech = "Good day! All systems are optimal. It is currently $timeStr on $dateStr. Device battery is at $level percent. MAYA neural core is online and ready for your commands."
            return DeviceActionResult(
                type = DeviceActionType.SYSTEM_BRIEFING,
                success = true,
                spokenResponse = speech,
                displayText = "MAYA Status Briefing\n• Time: $timeStr\n• Date: $dateStr\n• Battery: $level% (${if (isCharging) "Charging" else "Discharging"})\n• Systems: Fully Operational"
            )
        }

        // 4. Volume Control
        if (lower.contains("volume")) {
            if (lower.contains("up") || lower.contains("increase") || lower.contains("higher") || lower.contains("raise") || lower.contains("loud")) {
                adjustVolume(raise = true)
                return DeviceActionResult(
                    type = DeviceActionType.VOLUME_ADJUST,
                    success = true,
                    spokenResponse = "Media volume increased.",
                    displayText = "Volume Increased 🔊"
                )
            } else if (lower.contains("down") || lower.contains("decrease") || lower.contains("lower") || lower.contains("quiet")) {
                adjustVolume(raise = false)
                return DeviceActionResult(
                    type = DeviceActionType.VOLUME_ADJUST,
                    success = true,
                    spokenResponse = "Media volume decreased.",
                    displayText = "Volume Decreased 🔉"
                )
            } else if (lower.contains("mute")) {
                muteVolume()
                return DeviceActionResult(
                    type = DeviceActionType.VOLUME_ADJUST,
                    success = true,
                    spokenResponse = "Audio muted.",
                    displayText = "Volume Muted 🔇"
                )
            }
        }

        // 5. Timer / Alarm
        if (lower.contains("timer") || lower.contains("alarm")) {
            val digits = Regex("(\\d+)").find(lower)?.value?.toIntOrNull()
            if (lower.contains("timer") && digits != null) {
                val seconds = if (lower.contains("hour")) digits * 3600 else digits * 60
                val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                    putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                    putExtra(AlarmClock.EXTRA_MESSAGE, "MAYA Timer")
                    putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val launched = safeStartActivity(intent)
                return DeviceActionResult(
                    type = DeviceActionType.SET_TIMER,
                    success = launched,
                    spokenResponse = if (launched) "Setting a timer for $digits minutes." else "Timer set request ready.",
                    displayText = "Timer: $digits minutes ⏱️"
                )
            } else if (lower.contains("alarm")) {
                val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val launched = safeStartActivity(intent)
                return DeviceActionResult(
                    type = DeviceActionType.SET_ALARM,
                    success = launched,
                    spokenResponse = "Opening clock alarms.",
                    displayText = "Clock & Alarms ⏰"
                )
            }
        }

        // 6. Navigation / Maps
        if (lower.startsWith("navigate to") || lower.startsWith("directions to") || lower.startsWith("where is") || lower.contains("google maps")) {
            val location = lower.removePrefix("navigate to").removePrefix("directions to").removePrefix("where is").trim()
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode(location.ifEmpty { "nearby places" }))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val launched = safeStartActivity(intent)
            return DeviceActionResult(
                type = DeviceActionType.OPEN_MAPS,
                success = launched,
                spokenResponse = "Plotting course to $location.",
                displayText = "Navigating: $location 🗺️"
            )
        }

        // 7. App Launchers
        val appResult = handleAppLaunches(lower)
        if (appResult != null) return appResult

        // 8. Web Search
        if (lower.startsWith("search for") || lower.startsWith("google") || lower.startsWith("search web for")) {
            val searchTerm = lower.removePrefix("search for").removePrefix("google").removePrefix("search web for").trim()
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, searchTerm)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val launched = safeStartActivity(intent)
            return DeviceActionResult(
                type = DeviceActionType.SEARCH_WEB,
                success = launched,
                spokenResponse = "Searching the web for $searchTerm.",
                displayText = "Searching: $searchTerm 🔍"
            )
        }

        return null
    }

    private fun handleAppLaunches(lower: String): DeviceActionResult? {
        if (!lower.contains("open") && !lower.contains("launch") && !lower.contains("start")) {
            return null
        }

        val target = lower.removePrefix("open").removePrefix("launch").removePrefix("start").trim()

        val intent = when {
            target.contains("camera") || target.contains("photo") -> {
                Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("youtube") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("calculator") -> {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_CALCULATOR)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            target.contains("calendar") -> {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_CALENDAR)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            target.contains("settings") -> {
                Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("wifi") || target.contains("wi-fi") -> {
                Intent(Settings.ACTION_WIFI_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("bluetooth") -> {
                Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("browser") || target.contains("chrome") || target.contains("internet") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("clock") || target.contains("alarm") -> {
                Intent(AlarmClock.ACTION_SHOW_ALARMS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("dialer") || target.contains("phone") -> {
                Intent(Intent.ACTION_DIAL).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
            target.contains("messages") || target.contains("sms") -> {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            else -> null
        }

        if (intent != null) {
            val launched = safeStartActivity(intent)
            return DeviceActionResult(
                type = DeviceActionType.OPEN_APP,
                success = launched,
                spokenResponse = if (launched) "Opening $target for you." else "Unable to launch $target.",
                displayText = "Opened $target 🚀"
            )
        }

        return null
    }

    private fun safeStartActivity(intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun toggleTorch(turnOn: Boolean): Boolean {
        return try {
            val cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: cameraManager?.cameraIdList?.firstOrNull()

            if (cameraId != null && cameraManager != null) {
                cameraManager.setTorchMode(cameraId, turnOn)
                isTorchOn = turnOn
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun adjustVolume(raise: Boolean) {
        try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (raise) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
        } catch (_: Exception) {}
    }

    private fun muteVolume() {
        try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_MUTE,
                AudioManager.FLAG_SHOW_UI
            )
        } catch (_: Exception) {}
    }

    fun getBatteryInfo(): Pair<Int, Boolean> {
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            val level = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 85
            val isCharging = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val status = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            } else {
                false
            }
            Pair(level, isCharging)
        } catch (_: Exception) {
            Pair(90, false)
        }
    }
}
