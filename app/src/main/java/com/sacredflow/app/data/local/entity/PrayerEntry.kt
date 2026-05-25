package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_entries",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["useCase"]),
        Index(value = ["isDeleted"])
    ]
)
data class PrayerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipient: String,
    val useCase: String,
    val needs: List<String>,
    val tone: String,
    val length: String,
    val bodyText: String,
    val userContext: String?,
    val userNote: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val viewCount: Int = 0,
    val isDeleted: Boolean = false,
    /** User's reflection on how this prayer landed — one of "steady", "okay",
     *  "missed", or null if they haven't responded. Set by the LandedPicker on
     *  the Result and PrayerDetail screens. */
    val landed: String? = null
)
