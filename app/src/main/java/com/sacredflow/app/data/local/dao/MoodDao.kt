package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sacredflow.app.data.local.entity.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: MoodEntry): Long

    @Query("""
        SELECT * FROM mood_entries
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun observeRecent(limit: Int): Flow<List<MoodEntry>>

    @Query("""
        SELECT * FROM mood_entries
        WHERE createdAt >= :sinceMs
        ORDER BY createdAt DESC
    """)
    fun observeSince(sinceMs: Long): Flow<List<MoodEntry>>

    @Query("SELECT COUNT(*) FROM mood_entries")
    fun observeCount(): Flow<Int>
}
