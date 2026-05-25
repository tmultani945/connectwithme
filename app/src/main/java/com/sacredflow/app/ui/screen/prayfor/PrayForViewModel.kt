package com.sacredflow.app.ui.screen.prayfor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase
import com.sacredflow.app.domain.usecase.GeneratePrayerUseCase
import com.sacredflow.app.domain.usecase.ObservePreferencesUseCase
import com.sacredflow.app.ui.screen.generation.GenerationResultHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * "Prayer for someone you love" — builds a first-person prayer where the
 * speaker (the user) is interceding for [PrayForState.targetName] addressed to
 * the user's default recipient (Universe, God, etc.). Reuses the existing
 * generation pipeline with a context payload that frames the intercession.
 */
@HiltViewModel
class PrayForViewModel @Inject constructor(
    private val observePreferences: ObservePreferencesUseCase,
    private val generatePrayerUseCase: GeneratePrayerUseCase,
    private val preferenceRepository: PreferenceRepository,
    private val resultHolder: GenerationResultHolder
) : ViewModel() {

    private val _state = MutableStateFlow(PrayForState())
    val state: StateFlow<PrayForState> = _state.asStateFlow()

    private val _events = Channel<PrayForEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val prefs = observePreferences().first()
            _state.update {
                it.copy(
                    tone = Tone.fromKey(prefs.defaultTone),
                    isPlusUser = prefs.isPlusSubscriber
                )
            }
        }
    }

    fun onAction(action: PrayForAction) {
        when (action) {
            is PrayForAction.SetTargetName -> _state.update {
                it.copy(targetName = action.value.take(PrayForState.MAX_NAME_LENGTH))
            }
            is PrayForAction.SetSituation -> _state.update {
                it.copy(situation = action.value.take(PrayForState.MAX_SITUATION_LENGTH))
            }
            is PrayForAction.SetTone -> _state.update { it.copy(tone = action.value) }
            PrayForAction.Submit -> submit()
        }
    }

    private fun submit() {
        val snapshot = _state.value
        if (!snapshot.isValid) return
        if (snapshot.showOutOfQuota) {
            viewModelScope.launch { _events.send(PrayForEvent.ShowPaywall) }
            return
        }

        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val prefs = preferenceRepository.get()
            val senderName = prefs.userName.trim()
            val target = snapshot.targetName.trim()
            val situation = snapshot.situation.trim()

            // Build a userContext that makes the intercession unambiguous. The
            // worker's system prompt is already first-person; framing this as
            // "I'm holding [Target]" keeps the speaker as the user while naming
            // who the prayer is for.
            val builtContext = buildString {
                if (senderName.isNotEmpty()) {
                    append("My name is $senderName.")
                    append("\n")
                }
                append("I am praying for $target. I am holding $target in my thoughts today.")
                if (situation.isNotEmpty()) {
                    append("\n")
                    append("What's happening: $situation")
                }
                append("\n")
                append("Speak in first person — I am the one praying, asking on $target's behalf. Reference $target by name in the prayer. Do not narrate about me.")
            }

            // Recipient (who the prayer is addressed TO) — default from prefs,
            // typically Universe/God/Higher Self. The TARGET is folded into the
            // userContext above.
            val recipient = Recipient.fromName(prefs.defaultRecipient)

            val request = GenerationRequest(
                useCase = UseCase.Prayer.storageKey,
                recipient = recipient.displayName,
                recipientIsCustom = recipient.isCustom,
                needs = emptyList(),
                tone = snapshot.tone.storageKey,
                length = Length.Medium.storageKey,
                userContext = builtContext
            )

            val result = generatePrayerUseCase(request)
            resultHolder.put(request, result, prayForTarget = target)

            _state.update { it.copy(isSubmitting = false) }

            when (result) {
                is GenerationResult.Success, is GenerationResult.Fallback ->
                    _events.send(PrayForEvent.NavigateToLoading)
                is GenerationResult.SoftBlocked ->
                    _events.send(PrayForEvent.NavigateToLoading)
                is GenerationResult.RateLimited ->
                    _events.send(PrayForEvent.ShowPaywall)
                is GenerationResult.Error ->
                    _events.send(PrayForEvent.ShowError(result.userMessage))
            }
        }
    }
}
