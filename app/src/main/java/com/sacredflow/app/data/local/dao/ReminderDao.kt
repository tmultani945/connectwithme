package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sacredflow.app.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder?

    @Query("SELECT * FROM reminders ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY createdAt ASC LIMIT 1")
    fun observePrimary(): Flow<Reminder?>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY createdAt ASC LIMIT 1")
    suspend fun getPrimary(): Reminder?

    @Query("UPDATE reminders SET lastFiredAt = :firedAt WHERE id = :id")
    suspend fun setLastFiredAt(id: Long, firedAt: Long)
}
