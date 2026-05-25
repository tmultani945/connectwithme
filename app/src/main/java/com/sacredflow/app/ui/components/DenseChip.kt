package com.sacredflow.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.rememberSoftTap

/**
 * Pill-shaped filter chip with the warm-letterpress design: ink-dark fill when
 * selected, transparent with soft outline when not. Selected state shows a small
 * check icon. Used in onboarding, Create, and Detail metadata.
 */
@Composable
fun DenseChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val palette = LocalSacredPalette.current
    val softTap = rememberSoftTap()
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = { softTap(); onClick() },
        enabled = enabled,
        label = { Text(label) },
        shape = PillShape,
        leadingIcon = when {
            selected -> {
                { Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
            }
            leadingIcon != null -> {
                { Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(14.dp)) }
            }
            else -> null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = palette.ink,
            iconColor = palette.ink2,
            selectedContainerColor = palette.primaryInk,
            selectedLabelColor = MaterialTheme.colorScheme.background,
            selectedLeadingIconColor = MaterialTheme.colorScheme.background
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = palette.outlineSoft,
            selectedBorderColor = palette.primaryInk
        )
    )
}
