package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int>,
    val presetTemplateId: Long?,
    val workManagerRequestId: String?,
    val lastFiredAt: Long?,
    val createdAt: Long
)
