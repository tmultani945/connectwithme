package com.sacredflow.app.di

import com.sacredflow.app.data.repository.BillingRepository
import com.sacredflow.app.data.repository.GenerationRepository
import com.sacredflow.app.data.repository.GenerationRepositoryImpl
import com.sacredflow.app.data.repository.PlayBillingRepository
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.data.repository.PrayerRepositoryImpl
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.data.repository.PreferenceRepositoryImpl
import com.sacredflow.app.data.repository.ReminderRepository
import com.sacredflow.app.data.repository.ReminderRepositoryImpl
import com.sacredflow.app.data.repository.TemplateRepository
import com.sacredflow.app.data.repository.TemplateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindPreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository

    @Binds @Singleton
    abstract fun bindPrayerRepository(impl: PrayerRepositoryImpl): PrayerRepository

    @Binds @Singleton
    abstract fun bindTemplateRepository(impl: TemplateRepositoryImpl): TemplateRepository

    @Binds @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds @Singleton
    abstract fun bindGenerationRepository(impl: GenerationRepositoryImpl): GenerationRepository

    // Swap to a real PlayBillingRepository now that Batch 11 ships the implementation.
    @Binds @Singleton
    abstract fun bindBillingRepository(impl: PlayBillingRepository): BillingRepository
}
