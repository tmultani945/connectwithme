package com.sacredflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sacredflow.app.domain.model.Mood
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.rememberSoftTap

/**
 * Six word-based mood pills. Tap to select, tap again to clear. Selection is
 * optional — the generation works fine without it; when set, the descriptor is
 * folded into the generation prompt and the mood is recorded for history.
 */
@Composable
fun MoodPicker(
    selected: Mood?,
    onSelect: (Mood?) -> Unit,
    modifier: Modifier = Modifier
) {
    val softTap = rememberSoftTap()
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(Mood.entries) { mood ->
            MoodPill(
                mood = mood,
                selected = selected == mood,
                onClick = {
                    softTap()
                    // Tapping the selected pill clears the selection.
                    onSelect(if (selected == mood) null else mood)
                }
            )
        }
    }
}

@Composable
private fun MoodPill(
    mood: Mood,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalSacredPalette.current
    Box(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
            .background(
                if (selected) palette.primaryInk else MaterialTheme.colorScheme.background,
                PillShape
            )
            .border(
                width = 1.dp,
                color = if (selected) palette.primaryInk else palette.outlineSoft,
                shape = PillShape
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mood.displayName,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.background else palette.ink
        )
    }
}
