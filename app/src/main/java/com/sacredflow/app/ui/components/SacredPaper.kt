package com.sacredflow.app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs
import kotlin.random.Random

/**
 * Modifier that draws a subtle paper-grain texture overlay on top of the existing
 * content. The grain is procedurally generated — small random dots at low opacity —
 * to approximate the SVG noise filter the designer used.
 *
 * Apply via `Modifier.sacredPaper()`. Cheap to draw (≤ a couple hundred dots).
 */
@Composable
fun Modifier.sacredPaper(
    density: Float = 1.0f,
    seed: Int = 1
): Modifier {
    val dark = isSystemInDarkTheme()
    return this.drawWithCache {
        // Approximate "fractal noise at opacity 0.07" with a sparse stipple of dots.
        // For a 380x820 canvas, ~600 dots at 1px radius is barely perceptible at 7%
        // opacity but reads as texture.
        val w = size.width
        val h = size.height
        val area = w * h
        val dotCount = (area / 1800f * density).toInt().coerceIn(80, 1200)
        val rng = Random(seed)
        val dots = Array(dotCount) {
            Triple(
                rng.nextFloat() * w,
                rng.nextFloat() * h,
                rng.nextFloat() * 0.6f + 0.2f
            )
        }
        val dotColor = if (dark) Color(0xFFFFFFFF) else Color(0xFF3C2E14)
        val baseAlpha = if (dark) 0.04f else 0.06f
        onDrawWithContent {
            drawContent()
            for ((x, y, a) in dots) {
                drawCircle(
                    color = dotColor.copy(alpha = baseAlpha * a),
                    radius = 0.6f,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
    }
}

/**
 * Modifier that overlays a soft radial vignette — barely perceptible darkening
 * at the screen edges, like candlelight at the center. Used on Result, Detail,
 * and Loading screens.
 */
@Composable
fun Modifier.sacredVignette(
    strength: Float = 0.10f
): Modifier {
    val dark = isSystemInDarkTheme()
    val edge = if (dark) Color.Black.copy(alpha = 0.45f) else Color(0xFF50320F).copy(alpha = strength)
    return this.drawWithCache {
        val brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Transparent, edge),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.maxDimension * 0.75f
        )
        onDrawWithContent {
            drawContent()
            drawRect(brush)
        }
    }
}
