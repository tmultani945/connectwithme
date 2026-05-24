package com.sacredflow.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BreathingCircle(
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 160.dp,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "breathing")

    val scale by transition.animateFloat(
        initialValue = if (reducedMotion) 1f else 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = if (reducedMotion) 6000 else 10000
                0.85f at 0 using LinearEasing
                1.0f at 4000 using LinearEasing
                0.85f at 10000 using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 10000
                0.5f at 0
                1.0f at 4000
                0.5f at 10000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val effectiveScale = if (reducedMotion) 1f else scale
        val radius = (this.size.minDimension / 2f) * effectiveScale * 0.9f

        // Soft inner fill
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha * 0.25f), Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
        // Outer ring
        drawCircle(
            color = color.copy(alpha = alpha * 0.6f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
