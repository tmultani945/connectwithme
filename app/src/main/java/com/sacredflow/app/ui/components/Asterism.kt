package com.sacredflow.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette

/**
 * Three small dots in a horizontal row — a single visual punctuation mark used as
 * a divider, end-of-prayer mark, or quiet emphasis.
 *
 *     ∴
 *
 * Matches the designer's spec from the Result screen and Home screen.
 */
@Composable
fun Asterism(
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    color: Color = LocalSacredPalette.current.ink3,
    opacity: Float = 0.55f
) {
    Canvas(modifier = modifier.size(width = size * 3.2f, height = size)) {
        val dotRadius = this.size.height * 0.16f
        val gap = this.size.height * 0.6f
        val cy = this.size.height / 2f
        // Center three dots horizontally with `gap` between them.
        val totalWidth = dotRadius * 6 + gap * 2
        val startX = (this.size.width - totalWidth) / 2f + dotRadius
        for (i in 0..2) {
            drawCircle(
                color = color.copy(alpha = opacity),
                radius = dotRadius,
                center = Offset(x = startX + i * (dotRadius * 2 + gap), y = cy)
            )
        }
    }
}
