package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.repository.ReminderRepository
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: Long) {
        reminderRepository.delete(reminderId)
    }
}
