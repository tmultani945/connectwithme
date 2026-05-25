package com.sacredflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sacredflow.app.data.local.dao.FavoriteDao
import com.sacredflow.app.data.local.dao.GenerationHistoryDao
import com.sacredflow.app.data.local.dao.MoodDao
import com.sacredflow.app.data.local.dao.PrayerEntryDao
import com.sacredflow.app.data.local.dao.ReminderDao
import com.sacredflow.app.data.local.dao.TemplateDao
import com.sacredflow.app.data.local.dao.UserPreferenceDao
import com.sacredflow.app.data.local.entity.Favorite
import com.sacredflow.app.data.local.entity.GenerationHistory
import com.sacredflow.app.data.local.entity.MoodEntry
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
        GenerationHistory::class,
        MoodEntry::class
    ],
    version = 6,
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
    abstract fun moodDao(): MoodDao

    companion object {
        const val DATABASE_NAME = "sacred_flow.db"

        // v1 -> v2: adds daily-reflection bookkeeping columns to user_preferences.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN dailyReflectionDate TEXT")
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN dailyReflectionPrayerId INTEGER")
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN userName TEXT NOT NULL DEFAULT ''")
            }
        }

        // v2 -> v3: adds streak bookkeeping columns to user_preferences.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN streakCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN streakLastVisitDate TEXT")
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN streakGraceMonth TEXT")
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN streakGraceUsedThisMonth INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v3 -> v4: adds TTS voice preference.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_preferences ADD COLUMN voiceKey TEXT NOT NULL DEFAULT 'nova'")
            }
        }

        // v4 -> v5: adds the mood_entries table.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS mood_entries (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        moodKey TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        prayerEntryId INTEGER
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_mood_entries_createdAt ON mood_entries(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_mood_entries_moodKey ON mood_entries(moodKey)")
            }
        }

        // v5 -> v6: adds the `landed` reflection-feedback column to prayer_entries.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE prayer_entries ADD COLUMN landed TEXT")
            }
        }
    }
}
