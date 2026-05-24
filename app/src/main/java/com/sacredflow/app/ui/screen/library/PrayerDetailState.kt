package com.sacredflow.app.ui.screen.library

import com.sacredflow.app.data.local.entity.PrayerEntry

data class PrayerDetailState(
    val entry: PrayerEntry? = null,
    val isFavorited: Boolean = false,
    val noteDraft: String = "",
    val isEditingNote: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface PrayerDetailAction {
    data object ToggleFavorite : PrayerDetailAction
    data object StartEditingNote : PrayerDetailAction
    data object CancelEditingNote : PrayerDetailAction
    data class SetNoteDraft(val text: String) : PrayerDetailAction
    data object SaveNote : PrayerDetailAction
    data object Share : PrayerDetailAction
    data object Copy : PrayerDetailAction
    data object RequestDelete : PrayerDetailAction
    data object CancelDelete : PrayerDetailAction
    data object ConfirmDelete : PrayerDetailAction
}

sealed interface PrayerDetailEvent {
    data class ShareText(val text: String) : PrayerDetailEvent
    data class CopiedToClipboard(val text: String) : PrayerDetailEvent
    data class ShowSnackbar(val message: String) : PrayerDetailEvent
    data object NavigateBack : PrayerDetailEvent
}
