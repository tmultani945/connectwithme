package com.sacredflow.app.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.usecase.CompleteOnboardingUseCase
import com.sacredflow.app.ui.screen.generation.GenerationResultHolder
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
            is OnboardingAction.SetUseCase -> {
                _state.update { it.copy(useCase = action.useCase) }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("use_case"))
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
            is OnboardingAction.ToggleNeed -> {
                _state.update { current ->
                    val next = current.needs.toMutableSet()
                    if (action.need in next) next.remove(action.need)
                    else {
                        if (next.size >= Need.MAX_SELECTABLE) {
                            // Deselect the oldest by Set iteration order.
                            next.remove(next.first())
                        }
                        next.add(action.need)
                    }
                    current.copy(needs = next)
                }
            }
            is OnboardingAction.SetTone -> {
                _state.update { it.copy(tone = action.tone) }
                analytics.log(AnalyticsEvent.OnboardingStepCompleted("tone"))
            }
            is OnboardingAction.SetUserContext -> {
                _state.update {
                    it.copy(userContext = action.text.take(OnboardingState.MAX_CONTEXT_LENGTH))
                }
            }
            OnboardingAction.Submit -> submit()
        }
    }

    private fun submit() {
        val snapshot = _state.value
        val useCase = snapshot.useCase ?: return
        val recipient = snapshot.recipient ?: return
        val tone = snapshot.tone ?: return
        if (snapshot.needs.isEmpty()) return

        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = completeOnboarding(
                CompleteOnboardingUseCase.Input(
                    useCase = useCase,
                    recipient = recipient,
                    needs = snapshot.needs,
                    tone = tone,
                    userContext = snapshot.userContext.takeIf { it.isNotBlank() }
                )
            )

            // Store request + result for the Result screen.
            val request = GenerationRequest(
                useCase = useCase.storageKey,
                recipient = recipient.displayName,
                recipientIsCustom = recipient.isCustom,
                needs = snapshot.needs.map { it.storageKey },
                tone = tone.storageKey,
                length = Length.Medium.storageKey,
                userContext = snapshot.userContext.takeIf { it.isNotBlank() }
            )
            resultHolder.put(request, result)

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
