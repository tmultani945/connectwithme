package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "generation_history",
    foreignKeys = [
        ForeignKey(
            entity = PrayerEntry::class,
            parentColumns = ["id"],
            childColumns = ["prayerEntryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["prayerEntryId"]),
        Index(value = ["createdAt"])
    ]
)
data class GenerationHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prayerEntryId: Long?,
    val inputsJson: String,
    val outputText: String,
    val modelName: String,
    val promptVersion: String,
    val tokensIn: Int?,
    val tokensOut: Int?,
    val latencyMs: Long,
    val safetyFlagged: Boolean,
    val safetyReason: String?,
    val wasRegeneration: Boolean,
    val createdAt: Long
)
