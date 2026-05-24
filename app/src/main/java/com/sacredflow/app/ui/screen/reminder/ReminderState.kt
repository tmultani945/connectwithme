package com.sacredflow.app.ui.screen.reminder

data class ReminderState(
    val existingId: Long? = null,
    val isEnabled: Boolean = true,
    val hour: Int = 7,
    val minute: Int = 0,
    val daysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val needsNotificationPermission: Boolean = false,
    val isSaving: Boolean = false,
    val isLoaded: Boolean = false
) {
    val canSave: Boolean get() = daysOfWeek.isNotEmpty() && !isSaving
}

sealed interface ReminderAction {
    data class SetEnabled(val enabled: Boolean) : ReminderAction
    data class SetTime(val hour: Int, val minute: Int) : ReminderAction
    data class ToggleDay(val isoDay: Int) : ReminderAction
    data class SetDays(val days: Set<Int>) : ReminderAction
    data class SetPermissionGranted(val granted: Boolean) : ReminderAction
    data object Save : ReminderAction
    data object Delete : ReminderAction
    /** Fires a notification immediately so the user can verify permission + channel. */
    data object TestNow : ReminderAction
}

sealed interface ReminderEvent {
    data class ShowSnackbar(val message: String) : ReminderEvent
    data object RequestNotificationPermission : ReminderEvent
}
