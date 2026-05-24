package com.sacredflow.app.core.init

import android.util.Log
import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.data.local.seed.DatabaseSeeder
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.data.repository.ReminderRepository
import com.sacredflow.app.reminder.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val databaseSeeder: DatabaseSeeder,
    private val prayerRepository: PrayerRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val dispatchers: AppDispatchers
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    fun initialize() {
        scope.launch { runSafely("seed") { databaseSeeder.seedIfNeeded() } }
        scope.launch { runSafely("purge") { prayerRepository.purgeExpiredSoftDeletes() } }
        scope.launch { runSafely("rearm") { rearmReminder() } }
    }

    private suspend fun rearmReminder() {
        val reminder = reminderRepository.observePrimary().firstOrNull() ?: return
        if (reminder.isEnabled) {
            reminderScheduler.schedule(reminder)
        }
    }

    private suspend inline fun runSafely(tag: String, crossinline block: suspend () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            Log.w(TAG, "Initializer task '$tag' failed", t)
        }
    }

    companion object {
        private const val TAG = "SacredFlow/Init"
    }
}
