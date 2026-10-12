package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MayaVoiceState
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaMagenta
import com.example.ui.theme.MayaPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MayaOrbVisualizer(
    state: MayaVoiceState,
    audioAmplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "maya_orb_anim")

    // Slow continuous breathing
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Orbital ring rotations
    val ringRotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == MayaVoiceState.THINKING) 3000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rot_1"
    )

    val ringRotation2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == MayaVoiceState.THINKING) 2400 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rot_2"
    )

    val ripplePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_phase"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("maya_orb_button"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 4f

            // Dynamic scaling based on state and microphone amplitude
            val ampBoost = when (state) {
                MayaVoiceState.LISTENING -> audioAmplitude * 28f
                MayaVoiceState.SPEAKING -> sin(ripplePhase * 2 * PI).toFloat() * 10f + 8f
                MayaVoiceState.THINKING -> sin(ringRotation1 * PI / 180f).toFloat() * 6f
                else -> 0f
            }

            val currentCoreRadius = (baseRadius * pulse) + ampBoost

            // Primary Colors based on state
            val (coreColor1, coreColor2, ringColor) = when (state) {
                MayaVoiceState.LISTENING -> Triple(MayaCyan, Color(0xFF00E5FF), MayaCyan)
                MayaVoiceState.SPEAKING -> Triple(MayaMagenta, MayaPurple, MayaMagenta)
                MayaVoiceState.THINKING -> Triple(MayaPurple, MayaCyan, Color(0xFFE040FB))
                MayaVoiceState.ERROR -> Triple(Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFEF4444))
                MayaVoiceState.IDLE -> Triple(MayaCyan, MayaPurple, MayaCyan)
            }

            // Outer Aura Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreColor1.copy(alpha = if (state == MayaVoiceState.LISTENING) 0.45f else 0.25f),
                        coreColor2.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentCoreRadius * 1.9f
                ),
                radius = currentCoreRadius * 1.9f,
                center = center
            )

            // Dynamic Audio Harmonic Ripples during Speaking or Listening
            if (state == MayaVoiceState.SPEAKING || state == MayaVoiceState.LISTENING) {
                val rippleRadius = currentCoreRadius + (ripplePhase * baseRadius * 0.9f)
                val rippleAlpha = (1f - ripplePhase) * 0.7f
                drawCircle(
                    color = ringColor.copy(alpha = rippleAlpha),
                    radius = rippleRadius,
                    center = center,
                    style = Stroke(width = 2.5f)
                )

                val rippleRadius2 = currentCoreRadius + (((ripplePhase + 0.5f) % 1f) * baseRadius * 0.9f)
                val rippleAlpha2 = (1f - ((ripplePhase + 0.5f) % 1f)) * 0.6f
                drawCircle(
                    color = coreColor2.copy(alpha = rippleAlpha2),
                    radius = rippleRadius2,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }

            // Outer Dashed Geometric Ring
            drawCircle(
                color = ringColor.copy(alpha = 0.5f),
                radius = baseRadius * 1.55f,
                center = center,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), ringRotation1)
                )
            )

            // Middle Orbital Ring with Particle Satellites
            val midRadius = baseRadius * 1.35f
            drawCircle(
                color = coreColor2.copy(alpha = 0.35f),
                radius = midRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // 4 Orbital Satellite Nodes
            for (i in 0 until 4) {
                val angleRad = (ringRotation2 + (i * 90f)) * (PI / 180f)
                val nodeX = center.x + (midRadius * cos(angleRad)).toFloat()
                val nodeY = center.y + (midRadius * sin(angleRad)).toFloat()
                drawCircle(
                    color = if (i % 2 == 0) MayaCyan else MayaMagenta,
                    radius = 4f,
                    center = Offset(nodeX, nodeY)
                )
            }

            // Inner Fine Ring
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = currentCoreRadius * 1.1f,
                center = center,
                style = Stroke(
                    width = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f), -ringRotation1)
                )
            )

            // Luminescent Central Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        coreColor1,
                        coreColor2,
                        Color(0xFF0F172A)
                    ),
                    center = center.copy(
                        x = center.x - (currentCoreRadius * 0.15f),
                        y = center.y - (currentCoreRadius * 0.15f)
                    ),
                    radius = currentCoreRadius
                ),
                radius = currentCoreRadius,
                center = center
            )
        }
    }
}
