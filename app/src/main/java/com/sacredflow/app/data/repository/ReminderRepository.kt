package com.sacredflow.app.data.repository

import com.sacredflow.app.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun observePrimary(): Flow<Reminder?>
    suspend fun saveAndSchedule(reminder: Reminder): Long
    suspend fun delete(id: Long)
}
