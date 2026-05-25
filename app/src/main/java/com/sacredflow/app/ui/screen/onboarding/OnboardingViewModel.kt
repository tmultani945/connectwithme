package com.sacredflow.app.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.UseCase
import com.sacredflow.app.domain.usecase.CompleteOnboardingUseCase
import com.sacredflow.app.domain.usecase.SavePrayerUseCase
import com.sacredflow.app.ui.screen.generation.GenerationResultHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
class OnboardingViewModel @Inject constructor(
    private val completeOnboarding: CompleteOnboardingUseCase,
    private val resultHolder: GenerationResultHolder,
    private val savePrayerUseCase: SavePrayerUseCase,
    private val preferenceRepository: PreferenceRepository,
    private val clock: Clock,
    private val analytics: Analytics
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _events = Channel<OnboardingEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        analytics.log(AnalyticsEvent.OnboardingStarted)
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.SetPersona -> {
                // Persona maps to a (recipient, tone, topic) triple. Applying
                // it fills in the form so the user only needs to give their name.
                val p = action.persona
                _state.update {
                    it.copy(
                        persona = p,
                        recipient = Recipient.fromName(p.recipientDisplayName),
                        isCustomRecipientMode = false,
                        customRecipientDraft = "",
                        tone = p.tone,
                        topic = if (it.topic.isBlank()) p.topicSeed else it.topic
                    )
                }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("persona"))
            }
            is OnboardingAction.SetRecipient -> {
                _state.update {
                    it.copy(
                        recipient = action.recipient,
                        isCustomRecipientMode = action.recipient.isCustom
                    )
                }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("recipient"))
            }
            is OnboardingAction.EnableCustomRecipientMode -> {
                _state.update {
                    it.copy(
                        isCustomRecipientMode = action.enabled,
                        recipient = if (action.enabled) Recipient.Custom(it.customRecipientDraft) else null
                    )
                }
            }
            is OnboardingAction.SetCustomRecipientDraft -> {
                _state.update { current ->
                    current.copy(
                        customRecipientDraft = action.draft.take(30),
                        recipient = if (current.isCustomRecipientMode)
                            Recipient.Custom(action.draft.take(30)) else current.recipient
                    )
                }
            }
            is OnboardingAction.SetTopic -> {
                _state.update { it.copy(topic = action.text.take(OnboardingState.MAX_TOPIC_LENGTH)) }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("topic"))
            }
            is OnboardingAction.SetTone -> {
                _state.update { it.copy(tone = action.tone) }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("tone"))
            }
            is OnboardingAction.SetUserName -> {
                _state.update { it.copy(userName = action.text.take(OnboardingState.MAX_NAME_LENGTH)) }
            }
            is OnboardingAction.SetUserContext -> {
                _state.update {
                    it.copy(userContext = action.text.take(OnboardingState.MAX_CONTEXT_LENGTH))
                }
            }
            OnboardingAction.Submit -> submit()
        }
    }

    private fun localDateKey(ms: Long): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        return fmt.format(Date(ms))
    }

    private fun submit() {
        val snapshot = _state.value
        val recipient = snapshot.recipient ?: return
        val tone = snapshot.tone ?: return
        if (snapshot.topic.isBlank()) return

        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            // Mirror CreateViewModel: fold name + topic + extra context into the
            // single `userContext` slot. The Worker parses these apart server-side.
            val builtContext = buildString {
                if (snapshot.userName.isNotBlank()) {
                    append("My name is ${snapshot.userName.trim()}.")
                }
                if (snapshot.topic.isNotBlank()) {
                    if (isNotEmpty()) append("\n")
                    append("This is about: ${snapshot.topic.trim()}")
                }
                if (snapshot.userContext.isNotBlank()) {
                    if (isNotEmpty()) append("\n")
                    append(snapshot.userContext.trim())
                }
            }.takeIf { it.isNotBlank() }

            val result = completeOnboarding(
                CompleteOnboardingUseCase.Input(
                    useCase = UseCase.Prayer,
                    recipient = recipient,
                    needs = emptySet(),
                    tone = tone,
                    userContext = builtContext
                )
            )

            val request = GenerationRequest(
                useCase = UseCase.Prayer.storageKey,
                recipient = recipient.displayName,
                recipientIsCustom = recipient.isCustom,
                needs = emptyList(),
                tone = tone.storageKey,
                length = Length.Medium.storageKey,
                userContext = builtContext
            )

            // Persist user's name so the daily generator can address them by name
            // without re-asking. Cheap; runs whether or not the API call succeeded.
            if (snapshot.userName.isNotBlank()) {
                preferenceRepository.setUserName(snapshot.userName)
            }

            // For a Success: auto-save and mark as today's daily so the user
            // lands on Home with the daily card already populated (and we don't
            // burn a second API call generating a duplicate).
            val savedId: Long? = if (result is GenerationResult.Success) {
                val id = savePrayerUseCase(request, result)
                preferenceRepository.setDailyReflection(localDateKey(clock.nowMillis()), id)
                id
            } else null

            resultHolder.put(request, result, savedPrayerId = savedId)

            _state.update { it.copy(isSubmitting = false) }

            when (result) {
                is GenerationResult.Success -> _events.send(
                    OnboardingEvent.NavigateToResult(result.text, isFallback = false)
                )
                is GenerationResult.Fallback -> _events.send(
                    OnboardingEvent.NavigateToResult(result.text, isFallback = true)
                )
                is GenerationResult.SoftBlocked -> _events.send(
                    OnboardingEvent.NavigateToCrisisResources
                )
                is GenerationResult.RateLimited -> _events.send(
                    OnboardingEvent.ShowError(result.userMessage)
                )
                is GenerationResult.Error -> _events.send(
                    OnboardingEvent.ShowError(result.userMessage)
                )
            }
        }
    }
}
