package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One mood check-in. Logged each time the user marks a mood — typically when
 * generating a reflection, but the schema doesn't require [prayerEntryId] so
 * the table can hold standalone check-ins later (e.g. a "log mood" affordance
 * on Home or a daily widget).
 */
@Entity(
    tableName = "mood_entries",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["moodKey"])
    ]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodKey: String,
    val createdAt: Long,
    /** Optional link to the prayer that was generated with this mood. */
    val prayerEntryId: Long? = null
)
