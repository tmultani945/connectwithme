package com.sacredflow.app.ui.screen.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sacredflow.app.domain.usecase.DeletePrayerUseCase
import com.sacredflow.app.domain.usecase.GetPrayerDetailUseCase
import com.sacredflow.app.domain.usecase.ToggleFavoriteUseCase
import com.sacredflow.app.ui.navigation.PrayerDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPrayerDetail: GetPrayerDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deletePrayerUseCase: DeletePrayerUseCase
) : ViewModel() {

    private val prayerId: Long = savedStateHandle.toRoute<PrayerDetailRoute>().prayerId

    private val _state = MutableStateFlow(PrayerDetailState())
    val state: StateFlow<PrayerDetailState> = _state.asStateFlow()

    private val _events = Channel<PrayerDetailEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            getPrayerDetail.markViewed(prayerId)
        }
        viewModelScope.launch {
            getPrayerDetail(prayerId).collect { detail ->
                _state.update {
                    if (detail == null) {
                        it.copy(isLoading = false, entry = null)
                    } else {
                        it.copy(
                            isLoading = false,
                            entry = detail.entry,
                            isFavorited = detail.isFavorited,
                            noteDraft = if (it.isEditingNote) it.noteDraft
                            else detail.entry.userNote.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: PrayerDetailAction) {
        when (action) {
            PrayerDetailAction.ToggleFavorite -> viewModelScope.launch {
                toggleFavoriteUseCase(prayerId)
            }
            PrayerDetailAction.StartEditingNote -> _state.update {
                it.copy(isEditingNote = true, noteDraft = it.entry?.userNote.orEmpty())
            }
            PrayerDetailAction.CancelEditingNote -> _state.update {
                it.copy(isEditingNote = false, noteDraft = it.entry?.userNote.orEmpty())
            }
            is PrayerDetailAction.SetNoteDraft -> _state.update {
                it.copy(noteDraft = action.text)
            }
            PrayerDetailAction.SaveNote -> viewModelScope.launch {
                val draft = _state.value.noteDraft
                getPrayerDetail.updateNote(prayerId, draft.takeIf { it.isNotBlank() })
                _state.update { it.copy(isEditingNote = false) }
                _events.send(PrayerDetailEvent.ShowSnackbar("Note saved"))
            }
            PrayerDetailAction.Share -> viewModelScope.launch {
                val text = _state.value.entry?.bodyText.orEmpty()
                if (text.isNotEmpty()) _events.send(PrayerDetailEvent.ShareText(text))
            }
            PrayerDetailAction.Copy -> viewModelScope.launch {
                val text = _state.value.entry?.bodyText.orEmpty()
                if (text.isNotEmpty()) _events.send(PrayerDetailEvent.CopiedToClipboard(text))
            }
            PrayerDetailAction.RequestDelete -> _state.update { it.copy(showDeleteConfirm = true) }
            PrayerDetailAction.CancelDelete -> _state.update { it.copy(showDeleteConfirm = false) }
            PrayerDetailAction.ConfirmDelete -> viewModelScope.launch {
                _state.update { it.copy(showDeleteConfirm = false) }
                deletePrayerUseCase(prayerId)
                _events.send(PrayerDetailEvent.NavigateBack)
            }
        }
    }
}
