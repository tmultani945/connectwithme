package com.sacredflow.app.ui.screen.library

import com.sacredflow.app.data.repository.LibraryFilter
import com.sacredflow.app.domain.usecase.LibraryRow

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Empty(val activeFilter: LibraryFilter, val isSearching: Boolean) : LibraryUiState
    data class Content(
        val rows: List<LibraryRow>,
        val activeFilter: LibraryFilter,
        val searchQuery: String,
        val isSearchActive: Boolean,
        val selectedIds: Set<Long> = emptySet()
    ) : LibraryUiState {
        val isSelectionMode: Boolean get() = selectedIds.isNotEmpty()
    }
}

sealed interface LibraryAction {
    data class SetFilter(val filter: LibraryFilter) : LibraryAction
    data class SetSearchQuery(val query: String) : LibraryAction
    data class SetSearchActive(val active: Boolean) : LibraryAction
    data class ToggleSelection(val prayerId: Long) : LibraryAction
    data object ClearSelection : LibraryAction
    data object DeleteSelected : LibraryAction
    data class DeleteOne(val prayerId: Long) : LibraryAction
    data class UndoDelete(val prayerId: Long) : LibraryAction
}

sealed interface LibraryEvent {
    data class ShowUndoSnackbar(val prayerId: Long, val message: String) : LibraryEvent
    data class ShowSnackbar(val message: String) : LibraryEvent
}
