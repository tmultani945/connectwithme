package com.sacredflow.app.ui.screen.generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.usecase.GeneratePrayerUseCase
import com.sacredflow.app.domain.usecase.SavePrayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val resultHolder: GenerationResultHolder,
    private val savePrayerUseCase: SavePrayerUseCase,
    private val generatePrayerUseCase: GeneratePrayerUseCase,
    private val preferenceRepository: PreferenceRepository,
    private val prayerRepository: PrayerRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _events = Channel<ResultEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        // Load the user's preferred TTS voice so Speak after me uses it.
        viewModelScope.launch {
            val prefs = preferenceRepository.get()
            _state.update { it.copy(voiceKey = prefs.voiceKey) }
        }

        val snapshot = resultHolder.current.value
        if (snapshot == null) {
            _state.update { it.copy(noActiveResult = true) }
        } else {
            val request = snapshot.request
            // Onboarding pre-saves the first reflection so it can seed the daily card —
            // when that's the case, present this screen with isSaved already true so the
            // user doesn't accidentally double-save.
            val preSavedId = snapshot.savedPrayerId
            val forTarget = snapshot.prayForTarget
            when (val r = snapshot.result) {
                is GenerationResult.Success -> _state.update {
                    it.copy(
                        request = request,
                        text = r.text,
                        isFallback = false,
                        generationHistoryId = r.generationHistoryId,
                        isSaved = preSavedId != null,
                        savedPrayerId = preSavedId,
                        prayForTarget = forTarget
                    )
                }
                is GenerationResult.Fallback -> _state.update {
                    it.copy(
                        request = request,
                        text = r.text,
                        isFallback = true,
                        generationHistoryId = null,
                        prayForTarget = forTarget
                    )
                }
                else -> _state.update { it.copy(noActiveResult = true) }
            }
        }
    }

    fun onSave() {
        val snapshot = resultHolder.current.value ?: return
        val result = snapshot.result as? GenerationResult.Success ?: run {
            // Fallback save: build a synthetic save path. Skipped in MVP — fallback prayers
            // aren't saved automatically. The user is shown a different message.
            viewModelScope.launch {
                _events.send(ResultEvent.ShowSnackbar("Offline reflections aren't saved to your library."))
            }
            return
        }
        viewModelScope.launch {
            val id = savePrayerUseCase(snapshot.request, result)
            _state.update { it.copy(isSaved = true, savedPrayerId = id) }
            _events.send(ResultEvent.ShowSnackbar("Saved to Library"))
        }
    }

    fun onRegenerate() {
        val snapshot = resultHolder.current.value ?: return
        if (_state.value.isRegenerating) return
        _state.update { it.copy(isRegenerating = true, regenCount = it.regenCount + 1) }

        viewModelScope.launch {
            val request = snapshot.request.copy(
                isRegeneration = true,
                seed = System.currentTimeMillis()
            )
            val result = generatePrayerUseCase(request)
            resultHolder.put(request, result)

            when (result) {
                is GenerationResult.Success -> _state.update {
                    it.copy(
                        request = request,
                        text = result.text,
                        isFallback = false,
                        isSaved = false,
                        savedPrayerId = null,
                        generationHistoryId = result.generationHistoryId,
                        isRegenerating = false
                    )
                }
                is GenerationResult.Fallback -> _state.update {
                    it.copy(
                        request = request,
                        text = result.text,
                        isFallback = true,
                        isSaved = false,
                        savedPrayerId = null,
                        generationHistoryId = null,
                        isRegenerating = false
                    )
                }
                is GenerationResult.SoftBlocked -> {
                    _state.update { it.copy(isRegenerating = false) }
                    _events.send(ResultEvent.ShowSnackbar(result.userMessage))
                }
                is GenerationResult.RateLimited -> {
                    _state.update { it.copy(isRegenerating = false) }
                    _events.send(ResultEvent.ShowSnackbar(result.userMessage))
                }
                is GenerationResult.Error -> {
                    _state.update { it.copy(isRegenerating = false) }
                    _events.send(ResultEvent.ShowSnackbar(result.userMessage))
                }
            }
        }
    }

    fun onShare() {
        analytics.log(AnalyticsEvent.PrayerShared)
        viewModelScope.launch {
            _events.send(ResultEvent.ShareText(_state.value.text))
        }
    }

    fun onCopy() {
        viewModelScope.launch {
            _events.send(ResultEvent.CopiedToClipboard(_state.value.text))
        }
    }

    fun onDone() {
        resultHolder.clear()
        viewModelScope.launch { _events.send(ResultEvent.NavigateHome) }
    }

    /**
     * Records the user's "how did this land?" response. If the prayer hasn't
     * been saved yet, the first tap auto-saves it so the feedback has a
     * persistent home — the user shouldn't lose their reflection because they
     * forgot to tap Save.
     *
     * Tapping the currently selected option clears the response.
     */
    fun onLanded(landedKey: String?) {
        val snapshot = resultHolder.current.value ?: return
        val success = snapshot.result as? GenerationResult.Success ?: return

        val previousKey = _state.value.landed
        val nextKey = if (previousKey == landedKey) null else landedKey

        _state.update { it.copy(landed = nextKey) }

        viewModelScope.launch {
            val existingId = _state.value.savedPrayerId
            if (existingId != null) {
                prayerRepository.setLanded(existingId, nextKey)
            } else {
                // First feedback also persists the prayer so the response sticks.
                val id = savePrayerUseCase(snapshot.request, success)
                prayerRepository.setLanded(id, nextKey)
                _state.update { it.copy(isSaved = true, savedPrayerId = id) }
            }
        }
    }
}
