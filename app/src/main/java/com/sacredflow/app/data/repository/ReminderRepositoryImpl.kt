package com.sacredflow.app.data.repository

import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.dao.ReminderDao
import com.sacredflow.app.data.local.entity.Reminder
import com.sacredflow.app.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao,
    private val scheduler: ReminderScheduler,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : ReminderRepository {

    override fun observePrimary(): Flow<Reminder?> = dao.observePrimary()

    override suspend fun saveAndSchedule(reminder: Reminder): Long = withContext(dispatchers.io) {
        val now = clock.nowMillis()
        val toInsert = reminder.copy(
            createdAt = if (reminder.createdAt == 0L) now else reminder.createdAt
        )
        val id = dao.insert(toInsert)
        val saved = dao.getById(id) ?: error("Reminder $id missing after insert")
        val workId = scheduler.schedule(saved)
        if (workId.isNotEmpty() && saved.workManagerRequestId != workId) {
            dao.update(saved.copy(workManagerRequestId = workId))
        } else if (workId.isEmpty() && saved.workManagerRequestId != null) {
            dao.update(saved.copy(workManagerRequestId = null))
        }
        id
    }

    override suspend fun delete(id: Long) = withContext(dispatchers.io) {
        val existing = dao.getById(id) ?: return@withContext
        scheduler.cancel(existing)
        dao.delete(id)
    }
}
