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

    /**
     * Records that [prayerId] is the daily reflection for [localDate] (yyyy-MM-dd).
     * Read on Home to decide whether to surface the daily card or generate a new one.
     */
    suspend fun setDailyReflection(localDate: String, prayerId: Long)

    suspend fun setUserName(name: String)
}
