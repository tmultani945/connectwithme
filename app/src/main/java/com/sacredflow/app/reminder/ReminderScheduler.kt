package com.sacredflow.app.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sacredflow.app.data.local.entity.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun schedule(reminder: Reminder): String {
        if (!reminder.isEnabled) {
            cancel(reminder)
            return ""
        }
        val delay = computeInitialDelayMs(reminder)
        val data = Data.Builder()
            .putLong(KEY_REMINDER_ID, reminder.id)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(workTag(reminder.id))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName(reminder.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
        return request.id.toString()
    }

    fun cancel(reminder: Reminder) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName(reminder.id))
    }

    /**
     * Returns ms until the next valid firing time, given hour/minute and selected days-of-week.
     * Days-of-week use ISO numbering (1=Mon..7=Sun).
     */
    fun computeInitialDelayMs(reminder: Reminder, now: Long = System.currentTimeMillis()): Long {
        if (reminder.daysOfWeek.isEmpty()) return Long.MAX_VALUE

        val cal = Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = now }

        // Set candidate to today at H:M
        val candidate = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Walk forward up to 7 days to find the next selected day where the time is in the future.
        for (i in 0..6) {
            val isoDay = toIsoDayOfWeek(candidate.get(Calendar.DAY_OF_WEEK))
            if (isoDay in reminder.daysOfWeek && candidate.timeInMillis > now) {
                return candidate.timeInMillis - now
            }
            candidate.add(Calendar.DAY_OF_YEAR, 1)
        }
        return Long.MAX_VALUE
    }

    private fun toIsoDayOfWeek(calendarDay: Int): Int = when (calendarDay) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> error("Unexpected calendar day $calendarDay")
    }

    private fun uniqueWorkName(reminderId: Long): String = "$UNIQUE_WORK_PREFIX$reminderId"
    private fun workTag(reminderId: Long): String = "$WORK_TAG_PREFIX$reminderId"

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        private const val UNIQUE_WORK_PREFIX = "sacred_flow_reminder_"
        private const val WORK_TAG_PREFIX = "sacred_flow_reminder_tag_"
    }
}
