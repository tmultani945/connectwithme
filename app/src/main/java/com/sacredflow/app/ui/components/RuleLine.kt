package com.sacredflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette

/**
 * Hairline divider. With `ornament=true`, places an [Asterism] in the middle and
 * draws short hairlines either side. With `ornament=false`, a plain 1dp hairline.
 */
@Composable
fun RuleLine(
    modifier: Modifier = Modifier,
    ornament: Boolean = false,
    color: Color = LocalSacredPalette.current.outlineSoft,
    opacity: Float = 0.6f
) {
    val tint = color.copy(alpha = opacity)
    if (!ornament) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(tint)
        )
        return
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(tint))
        Asterism(size = 8.dp, opacity = 0.5f)
        Box(modifier = Modifier.weight(1f).height(1.dp).background(tint))
    }
}

/**
 * Short centered hairline — used as the "tiny rule" marker (e.g., above prayer text
 * on Result Variant A). 32dp wide, 1dp tall.
 */
@Composable
fun TinyRule(
    modifier: Modifier = Modifier,
    color: Color = LocalSacredPalette.current.ink,
    opacity: Float = 0.35f
) {
    Box(
        modifier = modifier
            .width(32.dp)
            .height(1.dp)
            .background(color.copy(alpha = opacity))
    )
}

/**
 * Hairline-with-dot divider:  ──•──
 *
 * More minimal than [RuleLine] with `ornament=true` (which centers a 3-dot Asterism).
 * Suits screen sections where Asterism feels too decorative — Library list dividers,
 * Settings section breaks, between Help FAQ items.
 */
@Composable
fun HairlineWithDot(
    modifier: Modifier = Modifier,
    dotSize: Dp = 4.dp,
    color: Color = LocalSacredPalette.current.outlineSoft,
    opacity: Float = 0.6f
) {
    val tint = color.copy(alpha = opacity)
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(tint))
        Box(modifier = Modifier.size(dotSize).background(tint, CircleShape))
        Box(modifier = Modifier.weight(1f).height(1.dp).background(tint))
    }
}

/**
 * End-dot — a single small circle, ink-color at low opacity. Used as an
 * end-of-prayer marker on the Result + Detail screens (replaces a closing
 * Asterism when something quieter is wanted).
 */
@Composable
fun EndDot(
    modifier: Modifier = Modifier,
    size: Dp = 6.dp,
    color: Color = LocalSacredPalette.current.ink3,
    opacity: Float = 0.5f
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color.copy(alpha = opacity), CircleShape)
    )
}
