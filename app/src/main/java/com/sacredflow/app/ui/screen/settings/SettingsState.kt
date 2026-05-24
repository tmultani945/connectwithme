package com.sacredflow.app.ui.screen.settings

import com.sacredflow.app.BuildConfig
import com.sacredflow.app.ui.theme.ThemeMode

data class SettingsState(
    val defaultRecipient: String = "Universe",
    val defaultTone: String = "gentle",
    val defaultUseCase: String = "Reflection",
    val themeMode: ThemeMode = ThemeMode.System,
    val useDynamicColor: Boolean = false,
    val isPlusSubscriber: Boolean = false,
    val totalSavedCount: Int = 0,
    val appVersion: String = BuildConfig.VERSION_NAME,
    val showThemePicker: Boolean = false,
    val showClearDataConfirm: Boolean = false
)

sealed interface SettingsAction {
    data class SetThemeMode(val mode: ThemeMode) : SettingsAction
    data class SetUseDynamicColor(val use: Boolean) : SettingsAction
    data object OpenThemePicker : SettingsAction
    data object CloseThemePicker : SettingsAction
    data object RequestClearData : SettingsAction
    data object CancelClearData : SettingsAction
    data object ConfirmClearData : SettingsAction
}

sealed interface SettingsEvent {
    data class ShowSnackbar(val message: String) : SettingsEvent
    data class OpenUrl(val url: String) : SettingsEvent
    data class ExportText(val content: String) : SettingsEvent
}
