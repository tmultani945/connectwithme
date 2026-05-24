package com.sacredflow.app.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.data.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _events = Channel<SplashEvent>(capacity = 1)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val onboardingComplete = dataStore.onboardingComplete.first()
            // Brief intentional hold so the splash registers as a brand moment, not a flash.
            kotlinx.coroutines.delay(MIN_SPLASH_HOLD_MS)
            _events.trySend(
                if (onboardingComplete) SplashEvent.NavigateToHome
                else SplashEvent.NavigateToOnboarding
            )
        }
    }

    companion object {
        private const val MIN_SPLASH_HOLD_MS = 800L
    }
}

sealed interface SplashEvent {
    data object NavigateToOnboarding : SplashEvent
    data object NavigateToHome : SplashEvent
}
