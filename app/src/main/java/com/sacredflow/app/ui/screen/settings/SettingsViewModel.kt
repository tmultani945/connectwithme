package com.sacredflow.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.billing.BillingState
import com.sacredflow.app.data.datastore.AppDataStore
import com.sacredflow.app.data.repository.BillingRepository
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.domain.usecase.ObservePreferencesUseCase
import com.sacredflow.app.domain.usecase.UpdatePreferencesUseCase
import com.sacredflow.app.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observePreferences: ObservePreferencesUseCase,
    private val updatePreferences: UpdatePreferencesUseCase,
    private val prayerRepository: PrayerRepository,
    private val billingRepository: BillingRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _events = Channel<SettingsEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val state: StateFlow<SettingsState> = combine(
        observePreferences(),
        prayerRepository.observeTotalCount(),
        billingRepository.state,
        dataStore.useDynamicColor
    ) { prefs, count, billing, useDynamic ->
        SettingsState(
            defaultRecipient = prefs.defaultRecipient,
            defaultTone = prefs.defaultTone,
            defaultUseCase = prefs.defaultUseCase,
            themeMode = ThemeMode.fromKey(prefs.themeMode),
            useDynamicColor = useDynamic,
            isPlusSubscriber = (billing as? BillingState.Ready)?.isSubscribed == true
                    || prefs.isPlusSubscriber,
            totalSavedCount = count,
            voiceKey = prefs.voiceKey
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState()
    )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetThemeMode -> viewModelScope.launch {
                updatePreferences.setThemeMode(action.mode)
            }
            is SettingsAction.SetUseDynamicColor -> viewModelScope.launch {
                dataStore.setUseDynamicColor(action.use)
            }
            is SettingsAction.SetVoice -> viewModelScope.launch {
                updatePreferences.setVoiceKey(action.key)
            }
            SettingsAction.OpenThemePicker, SettingsAction.CloseThemePicker -> Unit
            SettingsAction.RequestClearData -> Unit
            SettingsAction.CancelClearData -> Unit
            SettingsAction.ConfirmClearData -> viewModelScope.launch {
                // Stub for clear-all-data; deferred to v1.1 (involves DB nuking + DataStore reset).
                _events.send(SettingsEvent.ShowSnackbar("Clear data is coming in a future update."))
            }
        }
    }
}
