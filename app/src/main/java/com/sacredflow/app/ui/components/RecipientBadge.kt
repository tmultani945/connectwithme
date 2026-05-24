package com.sacredflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

/**
 * Small caps "— TO UNIVERSE —" label with hairlines either side. Replaces the plain
 * "TO RECIPIENT" badge from the old design.
 *
 * Used on Home cards, Result/Detail headers, Library list items.
 */
@Composable
fun RecipientBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalSacredPalette.current.primaryInk,
    hairlines: Boolean = true
) {
    val style = LocalSacredTypography.current.recipientBadge.copy(color = color)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hairlines) {
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(1.dp)
                    .background(color.copy(alpha = 0.5f))
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text.uppercase(), style = style, color = color)
        if (hairlines) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(1.dp)
                    .background(color.copy(alpha = 0.5f))
            )
        }
    }
}
