package com.sacredflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sacredflow.app.data.local.dao.FavoriteDao
import com.sacredflow.app.data.local.dao.GenerationHistoryDao
import com.sacredflow.app.data.local.dao.PrayerEntryDao
import com.sacredflow.app.data.local.dao.ReminderDao
import com.sacredflow.app.data.local.dao.TemplateDao
import com.sacredflow.app.data.local.dao.UserPreferenceDao
import com.sacredflow.app.data.local.entity.Favorite
import com.sacredflow.app.data.local.entity.GenerationHistory
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.local.entity.Reminder
import com.sacredflow.app.data.local.entity.Template
import com.sacredflow.app.data.local.entity.UserPreference

@Database(
    entities = [
        PrayerEntry::class,
        UserPreference::class,
        Template::class,
        Reminder::class,
        Favorite::class,
        GenerationHistory::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prayerEntryDao(): PrayerEntryDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun templateDao(): TemplateDao
    abstract fun reminderDao(): ReminderDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun generationHistoryDao(): GenerationHistoryDao

    companion object {
        const val DATABASE_NAME = "sacred_flow.db"
    }
}
