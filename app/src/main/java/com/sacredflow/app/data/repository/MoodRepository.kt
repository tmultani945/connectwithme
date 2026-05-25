package com.sacredflow.app.data.repository

import com.sacredflow.app.data.local.entity.MoodEntry
import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun observeRecent(limit: Int = 50): Flow<List<MoodEntry>>
    fun observeSince(sinceMs: Long): Flow<List<MoodEntry>>
    fun observeCount(): Flow<Int>

    /** Records a mood check-in. [prayerEntryId] links the mood to a prayer if
     *  it was logged at the same time. Returns the new row id. */
    suspend fun record(moodKey: String, prayerEntryId: Long? = null): Long
}
