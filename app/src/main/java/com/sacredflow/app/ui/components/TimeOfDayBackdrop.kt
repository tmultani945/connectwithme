package com.sacredflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Calendar

/**
 * A soft gradient banner that subtly tints the top of a screen based on time of day.
 * Used on the Home screen and Result screen — implies dawn/day/dusk/night light.
 *
 * - Dawn   (05–09): warm rose       (#F4D9C0)
 * - Day    (09–17): warm linen      (#F6EAD3)
 * - Dusk   (17–20): apricot         (#EAC5A8)
 * - Night  (20–05): deep linen      (#DCD2BA)
 */
@Composable
fun TimeOfDayBackdrop(
    modifier: Modifier = Modifier,
    height: Dp = 220.dp,
    hour: Int = currentHour(),
    opacity: Float = 0.7f
) {
    val top = when (hour) {
        in 5..8   -> Color(0xFFF4D9C0)
        in 9..16  -> Color(0xFFF6EAD3)
        in 17..19 -> Color(0xFFEAC5A8)
        else      -> Color(0xFFDCD2BA)
    }.copy(alpha = opacity)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    colors = listOf(top, Color.Transparent),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

private fun currentHour(): Int =
    Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
