package com.sacredflow.app.di

import android.content.Context
import androidx.room.Room
import com.sacredflow.app.data.local.AppDatabase
import com.sacredflow.app.data.local.dao.FavoriteDao
import com.sacredflow.app.data.local.dao.GenerationHistoryDao
import com.sacredflow.app.data.local.dao.PrayerEntryDao
import com.sacredflow.app.data.local.dao.ReminderDao
import com.sacredflow.app.data.local.dao.TemplateDao
import com.sacredflow.app.data.local.dao.UserPreferenceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides fun providePrayerEntryDao(db: AppDatabase): PrayerEntryDao = db.prayerEntryDao()
    @Provides fun provideUserPreferenceDao(db: AppDatabase): UserPreferenceDao = db.userPreferenceDao()
    @Provides fun provideTemplateDao(db: AppDatabase): TemplateDao = db.templateDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideGenerationHistoryDao(db: AppDatabase): GenerationHistoryDao =
        db.generationHistoryDao()
}
