package com.sacredflow.app.data.repository

import com.sacredflow.app.data.local.entity.UserPreference
import com.sacredflow.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    fun observe(): Flow<UserPreference>

    suspend fun get(): UserPreference

    suspend fun update(transform: (UserPreference) -> UserPreference)

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun markOnboardingComplete()

    suspend fun addCustomRecipient(name: String)

    suspend fun incrementFreeGenerationsToday()

    suspend fun resetQuotaIfNewDay()

    suspend fun setSubscriberStatus(isPlus: Boolean)
}
