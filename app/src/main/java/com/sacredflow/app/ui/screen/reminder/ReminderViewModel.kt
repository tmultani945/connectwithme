package com.sacredflow.app.ui.screen.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.data.local.entity.Reminder
import com.sacredflow.app.data.repository.ReminderRepository
import com.sacredflow.app.domain.usecase.CancelReminderUseCase
import com.sacredflow.app.domain.usecase.ScheduleReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> = _state.asStateFlow()

    private val _events = Channel<ReminderEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val existing = reminderRepository.observePrimary().first()
            _state.update {
                if (existing == null) it.copy(isLoaded = true)
                else it.copy(
                    existingId = existing.id,
                    isEnabled = existing.isEnabled,
                    hour = existing.hour,
                    minute = existing.minute,
                    daysOfWeek = existing.daysOfWeek.toSet(),
                    isLoaded = true
                )
            }
        }
    }

    fun onAction(action: ReminderAction) {
        when (action) {
            is ReminderAction.SetEnabled -> _state.update { it.copy(isEnabled = action.enabled) }
            is ReminderAction.SetTime -> _state.update {
                it.copy(hour = action.hour, minute = action.minute)
            }
            is ReminderAction.ToggleDay -> _state.update { current ->
                val next = current.daysOfWeek.toMutableSet()
                if (action.isoDay in next) next.remove(action.isoDay) else next.add(action.isoDay)
                current.copy(daysOfWeek = next)
            }
            is ReminderAction.SetPermissionGranted -> _state.update {
                it.copy(needsNotificationPermission = !action.granted)
            }
            ReminderAction.Save -> save()
            ReminderAction.Delete -> delete()
        }
    }

    private fun save() {
        val snapshot = _state.value
        if (snapshot.needsNotificationPermission && snapshot.isEnabled) {
            viewModelScope.launch { _events.send(ReminderEvent.RequestNotificationPermission) }
            return
        }
        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val reminder = Reminder(
                id = snapshot.existingId ?: 0L,
                isEnabled = snapshot.isEnabled,
                hour = snapshot.hour,
                minute = snapshot.minute,
                daysOfWeek = snapshot.daysOfWeek.sorted(),
                presetTemplateId = null,
                workManagerRequestId = null,
                lastFiredAt = null,
                createdAt = 0L
            )
            val id = scheduleReminderUseCase(reminder)
            _state.update { it.copy(isSaving = false, existingId = id) }
            _events.send(ReminderEvent.ShowSnackbar(
                if (snapshot.isEnabled) "Reminder set" else "Reminder paused"
            ))
        }
    }

    private fun delete() {
        val id = _state.value.existingId ?: return
        viewModelScope.launch {
            cancelReminderUseCase(id)
            _state.update {
                ReminderState(isLoaded = true)
            }
            _events.send(ReminderEvent.ShowSnackbar("Reminder removed"))
        }
    }
}
