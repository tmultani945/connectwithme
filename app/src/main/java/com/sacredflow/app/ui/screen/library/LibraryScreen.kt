package com.sacredflow.app.ui.screen.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.data.repository.LibraryFilter
import com.sacredflow.app.ui.components.EmptyState
import com.sacredflow.app.ui.components.PrayerCard
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onOpenPrayer: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LibraryEvent.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Undo",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onAction(LibraryAction.UndoDelete(event.prayerId))
                    }
                }
                is LibraryEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().sacredPaper(density = 0.5f)) {
            Column(modifier = Modifier.fillMaxSize()) {
                LibraryHeader(state = state, onAction = viewModel::onAction)
                when (val s = state) {
                    LibraryUiState.Loading -> Unit
                    is LibraryUiState.Empty -> EmptyLibraryView(s)
                    is LibraryUiState.Content -> LibraryContent(
                        state = s,
                        onAction = viewModel::onAction,
                        onOpenPrayer = onOpenPrayer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryHeader(
    state: LibraryUiState,
    onAction: (LibraryAction) -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val isSelectionMode = (state as? LibraryUiState.Content)?.isSelectionMode == true
    val selectionCount = (state as? LibraryUiState.Content)?.selectedIds?.size ?: 0
    val isSearchActive = (state as? LibraryUiState.Content)?.isSearchActive == true
    val searchQuery = (state as? LibraryUiState.Content)?.searchQuery ?: ""
    val totalCount = (state as? LibraryUiState.Content)?.rows?.size ?: 0
    val favCount = (state as? LibraryUiState.Content)?.rows?.count { it.isFavorited } ?: 0

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when {
                isSelectionMode -> Column {
                    Text(text = "SELECTED", style = typo.overline, color = palette.ink3)
                    Text(
                        text = "$selectionCount items",
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.primaryInk
                    )
                }
                isSearchActive -> {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { onAction(LibraryAction.SetSearchQuery(it)) },
                        placeholder = { Text("Search saved prayers") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
                else -> Column {
                    Text(text = "YOUR COLLECTION", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.primaryInk
                    )
                }
            }
            CircleIconButton(
                icon = when {
                    isSelectionMode -> Icons.Outlined.Delete
                    isSearchActive -> Icons.Outlined.Close
                    else -> Icons.Outlined.Search
                },
                contentDescription = when {
                    isSelectionMode -> "Delete selected"
                    isSearchActive -> "Close search"
                    else -> "Search"
                },
                onClick = {
                    when {
                        isSelectionMode -> onAction(LibraryAction.DeleteSelected)
                        isSearchActive -> onAction(LibraryAction.SetSearchActive(false))
                        else -> onAction(LibraryAction.SetSearchActive(true))
                    }
                }
            )
        }
        if (!isSelectionMode && !isSearchActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$totalCount reflections · $favCount favorites",
                style = typo.overline,
                color = palette.ink3
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val palette = LocalSacredPalette.current
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, palette.outlineSoft),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = palette.ink2
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun LibraryContent(
    state: LibraryUiState.Content,
    onAction: (LibraryAction) -> Unit,
    onOpenPrayer: (Long) -> Unit
) {
    val palette = LocalSacredPalette.current
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { filter ->
                FilterChip(
                    selected = state.activeFilter == filter,
                    onClick = { onAction(LibraryAction.SetFilter(filter)) },
                    label = { Text(filterLabel(filter)) },
                    shape = PillShape,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        labelColor = palette.ink,
                        selectedContainerColor = palette.primaryInk,
                        selectedLabelColor = MaterialTheme.colorScheme.background
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = state.activeFilter == filter,
                        borderColor = palette.outlineSoft,
                        selectedBorderColor = palette.primaryInk
                    )
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.rows, key = { it.entry.id }) { row ->
                val entry = row.entry
                val isSelected = entry.id in state.selectedIds
                val accent = when ((entry.id % 3).toInt()) {
                    0 -> palette.primaryInk
                    1 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.tertiary
                }
                PrayerCard(
                    recipient = entry.recipient,
                    bodyPreview = entry.bodyText,
                    createdAt = entry.createdAt,
                    isFavorited = row.isFavorited,
                    onClick = {
                        if (state.isSelectionMode) onAction(LibraryAction.ToggleSelection(entry.id))
                        else onOpenPrayer(entry.id)
                    },
                    onLongClick = { onAction(LibraryAction.ToggleSelection(entry.id)) },
                    isSelected = isSelected,
                    accentColor = accent,
                    wide = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EmptyLibraryView(state: LibraryUiState.Empty) {
    val pad = Modifier.padding(horizontal = 24.dp)
    when {
        state.isSearching -> EmptyState(
            title = "Nothing matches that search.",
            subtitle = "Try a different word, or clear search to see everything.",
            modifier = pad
        )
        state.activeFilter == LibraryFilter.Favorites -> EmptyState(
            title = "No favorites yet.",
            subtitle = "Tap the star on any prayer to save it here.",
            modifier = pad
        )
        else -> EmptyState(
            title = "Nothing saved yet.",
            subtitle = "Your first prayer is one tap away.",
            modifier = pad
        )
    }
}

private val filterOptions = listOf(
    LibraryFilter.All,
    LibraryFilter.Favorites,
    LibraryFilter.Prayer,
    LibraryFilter.Intention,
    LibraryFilter.Gratitude,
    LibraryFilter.Healing,
    LibraryFilter.Reflection
)

private fun filterLabel(filter: LibraryFilter): String = when (filter) {
    LibraryFilter.All -> "All"
    LibraryFilter.Favorites -> "Favorites"
    LibraryFilter.Prayer -> "Prayer"
    LibraryFilter.Intention -> "Intention"
    LibraryFilter.Gratitude -> "Gratitude"
    LibraryFilter.Healing -> "Healing"
    LibraryFilter.Reflection -> "Reflection"
    LibraryFilter.Custom -> "Custom"
}
