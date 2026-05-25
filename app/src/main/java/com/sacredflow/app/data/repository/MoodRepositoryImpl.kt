package com.sacredflow.app.data.repository

import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.dao.MoodDao
import com.sacredflow.app.data.local.entity.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepositoryImpl @Inject constructor(
    private val dao: MoodDao,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : MoodRepository {

    override fun observeRecent(limit: Int): Flow<List<MoodEntry>> = dao.observeRecent(limit)

    override fun observeSince(sinceMs: Long): Flow<List<MoodEntry>> = dao.observeSince(sinceMs)

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun record(moodKey: String, prayerEntryId: Long?): Long =
        withContext(dispatchers.io) {
            dao.insert(
                MoodEntry(
                    moodKey = moodKey,
                    createdAt = clock.nowMillis(),
                    prayerEntryId = prayerEntryId
                )
            )
        }
}
