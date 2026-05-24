package com.sacredflow.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.PillShape

data class ChipItem<T>(
    val value: T,
    val label: String,
    val enabled: Boolean = true,
    val locked: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChipCloud(
    items: List<ChipItem<T>>,
    selected: Set<T>,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item.value in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(item.value) },
                enabled = item.enabled,
                shape = PillShape,
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else null,
                trailingIcon = if (item.locked) {
                    {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = "Plus only",
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = palette.ink,
                    iconColor = palette.ink2,
                    selectedContainerColor = palette.primaryInk,
                    selectedLabelColor = MaterialTheme.colorScheme.background,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.background,
                    selectedTrailingIconColor = MaterialTheme.colorScheme.background
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = item.enabled,
                    selected = isSelected,
                    borderColor = palette.outlineSoft,
                    selectedBorderColor = palette.primaryInk,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp
                )
            )
        }
    }
}
