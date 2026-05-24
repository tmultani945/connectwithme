package com.sacredflow.app.di

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.LogcatAnalytics
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.core.time.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock()

    @Provides
    @Singleton
    fun provideAnalytics(): Analytics = LogcatAnalytics()

    // InstallIdProvider, FallbackContentProvider, CrisisKeywordDetector,
    // CrisisResourcesProvider, ReminderScheduler, ReminderNotificationBuilder
    // are all @Inject-constructor + @Singleton — no module entries needed.
}
