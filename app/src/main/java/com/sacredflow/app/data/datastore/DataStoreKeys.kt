package com.sacredflow.app.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val INSTALL_ID = stringPreferencesKey("install_id")
    val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
}
