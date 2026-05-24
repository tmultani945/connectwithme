package com.sacredflow.app.ui.screen.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.data.safety.CrisisResources
import com.sacredflow.app.data.safety.CrisisResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrisisResourcesViewModel @Inject constructor(
    private val provider: CrisisResourcesProvider
) : ViewModel() {

    private val _state = MutableStateFlow<CrisisResources?>(null)
    val state: StateFlow<CrisisResources?> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = provider.resourcesForCurrentLocale()
        }
    }
}
