package com.sacredflow.app.ui.screen.generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
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
    private val analytics: Analytics
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _events = Channel<ResultEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        val snapshot = resultHolder.current.value
        if (snapshot == null) {
            _state.update { it.copy(noActiveResult = true) }
        } else {
            val request = snapshot.request
            when (val r = snapshot.result) {
                is GenerationResult.Success -> _state.update {
                    it.copy(
                        request = request,
                        text = r.text,
                        isFallback = false,
                        generationHistoryId = r.generationHistoryId
                    )
                }
                is GenerationResult.Fallback -> _state.update {
                    it.copy(
                        request = request,
                        text = r.text,
                        isFallback = true,
                        generationHistoryId = null
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
}
