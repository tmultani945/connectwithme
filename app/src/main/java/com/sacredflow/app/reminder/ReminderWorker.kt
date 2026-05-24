package com.sacredflow.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.dao.ReminderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val scheduler: ReminderScheduler,
    private val notificationBuilder: ReminderNotificationBuilder,
    private val analytics: Analytics,
    private val clock: Clock
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getLong(ReminderScheduler.KEY_REMINDER_ID, -1L)
        if (reminderId == -1L) return Result.failure()

        val reminder = reminderDao.getById(reminderId) ?: return Result.success()
        if (!reminder.isEnabled) return Result.success()

        notificationBuilder.post(reminderId)
        analytics.log(AnalyticsEvent.ReminderFired)

        reminderDao.setLastFiredAt(reminderId, clock.nowMillis())

        // Re-schedule for the next valid time.
        scheduler.schedule(reminder)

        return Result.success()
    }
}
