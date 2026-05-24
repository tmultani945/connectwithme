package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sacredflow.app.data.local.entity.GenerationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(history: GenerationHistory): Long

    @Query("UPDATE generation_history SET prayerEntryId = :prayerId WHERE id = :historyId")
    suspend fun linkToPrayer(historyId: Long, prayerId: Long)

    @Query("SELECT * FROM generation_history WHERE id = :id")
    suspend fun getById(id: Long): GenerationHistory?

    @Query("SELECT COUNT(*) FROM generation_history WHERE createdAt >= :since")
    suspend fun countSince(since: Long): Int

    @Query("SELECT COUNT(*) FROM generation_history")
    fun observeTotalCount(): Flow<Int>

    @Query("DELETE FROM generation_history WHERE createdAt < :before")
    suspend fun purgeOlderThan(before: Long): Int
}
