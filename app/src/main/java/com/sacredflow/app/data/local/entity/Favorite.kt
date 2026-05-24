package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = PrayerEntry::class,
            parentColumns = ["id"],
            childColumns = ["prayerEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["prayerEntryId"], unique = true)]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prayerEntryId: Long,
    val createdAt: Long
)
