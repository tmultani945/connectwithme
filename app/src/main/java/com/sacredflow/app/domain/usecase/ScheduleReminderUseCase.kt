package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.local.entity.Reminder
import com.sacredflow.app.data.repository.ReminderRepository
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val analytics: Analytics
) {
    suspend operator fun invoke(reminder: Reminder): Long {
        val id = reminderRepository.saveAndSchedule(reminder)
        if (reminder.isEnabled) analytics.log(AnalyticsEvent.ReminderSet)
        return id
    }
}
