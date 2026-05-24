package com.sacredflow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette

/**
 * Action affordance for the Result / Detail action rows:
 *  - A 44dp circular icon with a hairline outline on the surface
 *  - A small label beneath
 *
 *     ⓒ
 *    Save
 */
@Composable
fun IconActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = null,
    selected: Boolean = false
) {
    val palette = LocalSacredPalette.current
    val effectiveTint = tint ?: palette.primaryInk
    val borderColor = if (selected) palette.primaryInk.copy(alpha = 0.6f) else palette.outlineSoft
    val background = if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, borderColor),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = background,
                contentColor = effectiveTint,
                disabledContainerColor = background,
                disabledContentColor = effectiveTint.copy(alpha = 0.4f)
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) palette.ink2 else palette.ink3
        )
    }
}
