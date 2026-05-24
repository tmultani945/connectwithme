package com.sacredflow.app.ui.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.data.repository.LibraryFilter
import com.sacredflow.app.domain.usecase.DeletePrayerUseCase
import com.sacredflow.app.domain.usecase.GetLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibraryUseCase: GetLibraryUseCase,
    private val deletePrayerUseCase: DeletePrayerUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(LibraryFilter.All)
    private val searchQuery = MutableStateFlow("")
    private val searchActive = MutableStateFlow(false)
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())

    private val _events = Channel<LibraryEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val rowsFlow = combine(filter, searchQuery.debounce(150)) { f, q -> f to q }
        .distinctUntilChanged()
        .flatMapLatest { (f, q) -> getLibraryUseCase(f, q) }

    val state: StateFlow<LibraryUiState> = combine(
        rowsFlow,
        filter,
        searchQuery,
        searchActive,
        selectedIds
    ) { rows, f, q, active, ids ->
        when {
            rows.isEmpty() -> LibraryUiState.Empty(
                activeFilter = f,
                isSearching = active && q.isNotBlank()
            )
            else -> LibraryUiState.Content(
                rows = rows,
                activeFilter = f,
                searchQuery = q,
                isSearchActive = active,
                selectedIds = ids
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = LibraryUiState.Loading
    )

    fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.SetFilter -> filter.value = action.filter
            is LibraryAction.SetSearchQuery -> searchQuery.value = action.query
            is LibraryAction.SetSearchActive -> {
                searchActive.value = action.active
                if (!action.active) searchQuery.value = ""
            }
            is LibraryAction.ToggleSelection -> selectedIds.update { current ->
                if (action.prayerId in current) current - action.prayerId
                else current + action.prayerId
            }
            LibraryAction.ClearSelection -> selectedIds.value = emptySet()
            LibraryAction.DeleteSelected -> deleteSelected()
            is LibraryAction.DeleteOne -> deleteOne(action.prayerId)
            is LibraryAction.UndoDelete -> undoDelete(action.prayerId)
        }
    }

    private fun deleteOne(prayerId: Long) {
        viewModelScope.launch {
            deletePrayerUseCase(prayerId)
            _events.send(LibraryEvent.ShowUndoSnackbar(prayerId, "Deleted"))
        }
    }

    private fun deleteSelected() {
        val ids = selectedIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { deletePrayerUseCase(it) }
            selectedIds.value = emptySet()
            _events.send(LibraryEvent.ShowSnackbar("${ids.size} deleted"))
        }
    }

    private fun undoDelete(prayerId: Long) {
        viewModelScope.launch {
            deletePrayerUseCase.undo(prayerId)
        }
    }
}
