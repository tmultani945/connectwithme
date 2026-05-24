package com.sacredflow.app.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[DataStoreKeys.THEME_MODE] ?: "system"
    }

    val onboardingComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DataStoreKeys.ONBOARDING_COMPLETE] ?: false
    }

    val useDynamicColor: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DataStoreKeys.USE_DYNAMIC_COLOR] ?: false
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[DataStoreKeys.THEME_MODE] = mode }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[DataStoreKeys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setUseDynamicColor(use: Boolean) {
        dataStore.edit { it[DataStoreKeys.USE_DYNAMIC_COLOR] = use }
    }

    suspend fun getInstallId(): String? = dataStore.data.first()[DataStoreKeys.INSTALL_ID]

    suspend fun setInstallId(id: String) {
        dataStore.edit { it[DataStoreKeys.INSTALL_ID] = id }
    }
}
