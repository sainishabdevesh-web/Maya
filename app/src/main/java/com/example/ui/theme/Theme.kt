package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MayaDarkColorScheme = darkColorScheme(
    primary = MayaCyan,
    onPrimary = MayaSpaceBlack,
    primaryContainer = MayaPurpleDark,
    onPrimaryContainer = MayaCyan,
    secondary = MayaMagenta,
    onSecondary = MayaSpaceBlack,
    secondaryContainer = MayaCardSurface,
    onSecondaryContainer = MayaMagenta,
    tertiary = MayaPurple,
    onTertiary = MayaTextPrimary,
    background = MayaSpaceBlack,
    onBackground = MayaTextPrimary,
    surface = MayaDarkSurface,
    onSurface = MayaTextPrimary,
    surfaceVariant = MayaCardSurface,
    onSurfaceVariant = MayaTextSecondary,
    outline = MayaBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent high-tech cyberpunk sci-fi identity
    content: @Composable () -> Unit
) {
    val colorScheme = MayaDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MayaSpaceBlack.toArgb()
                window.navigationBarColor = MayaSpaceBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
