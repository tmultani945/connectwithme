package com.sacredflow.app.ui.screen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
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

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val observePreferences: ObservePreferencesUseCase,
    private val generatePrayerUseCase: GeneratePrayerUseCase,
    private val resultHolder: GenerationResultHolder
) : ViewModel() {

    private val _state = MutableStateFlow(CreateState())
    val state: StateFlow<CreateState> = _state.asStateFlow()

    private val _events = Channel<CreateEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val prefs = observePreferences().first()
            _state.update {
                it.copy(
                    useCase = UseCase.fromKey(prefs.defaultUseCase),
                    recipient = Recipient.fromName(prefs.defaultRecipient),
                    customRecipientChoices = prefs.customRecipients,
                    isCustomRecipientMode = Recipient.fromName(prefs.defaultRecipient) is Recipient.Custom,
                    customRecipientDraft = if (Recipient.fromName(prefs.defaultRecipient) is Recipient.Custom)
                        prefs.defaultRecipient else "",
                    needs = Need.fromKeys(prefs.lastUsedNeeds),
                    tone = Tone.fromKey(prefs.defaultTone),
                    length = Length.fromKey(prefs.defaultLength),
                    isPlusUser = prefs.isPlusSubscriber,
                    remainingFreeToday = com.sacredflow.app.data.local.entity.UserPreference.FREE_DAILY_QUOTA,
                    isPrefilled = true
                )
            }
        }
    }

    fun onAction(action: CreateAction) {
        when (action) {
            is CreateAction.SetUseCase -> _state.update { it.copy(useCase = action.value) }
            is CreateAction.SetRecipient -> _state.update {
                it.copy(recipient = action.value, isCustomRecipientMode = action.value.isCustom)
            }
            is CreateAction.EnableCustomRecipientMode -> _state.update {
                it.copy(
                    isCustomRecipientMode = action.enabled,
                    recipient = if (action.enabled)
                        Recipient.Custom(it.customRecipientDraft)
                    else
                        Recipient.BuiltIn("Universe")
                )
            }
            is CreateAction.SetCustomRecipientDraft -> _state.update { current ->
                val trimmed = action.draft.take(30)
                current.copy(
                    customRecipientDraft = trimmed,
                    recipient = if (current.isCustomRecipientMode)
                        Recipient.Custom(trimmed) else current.recipient
                )
            }
            is CreateAction.ToggleNeed -> _state.update { current ->
                val next = current.needs.toMutableSet()
                if (action.value in next) next.remove(action.value)
                else {
                    if (next.size >= Need.MAX_SELECTABLE) next.remove(next.first())
                    next.add(action.value)
                }
                current.copy(needs = next)
            }
            is CreateAction.SetTone -> _state.update { it.copy(tone = action.value) }
            is CreateAction.SetLength -> {
                val target = action.value
                _state.update { current ->
                    if (target.isPlusOnly && !current.isPlusUser) {
                        // Don't change length, but emit paywall event.
                        viewModelScope.launch { _events.send(CreateEvent.ShowPaywall) }
                        current
                    } else current.copy(length = target)
                }
            }
            is CreateAction.SetTopic -> _state.update {
                it.copy(topic = action.text.take(CreateState.MAX_TOPIC_LENGTH))
            }
            is CreateAction.SetUserName -> _state.update {
                it.copy(userName = action.text.take(CreateState.MAX_NAME_LENGTH))
            }
            is CreateAction.SetUserContext -> _state.update {
                it.copy(userContext = action.text.take(CreateState.MAX_CONTEXT_LENGTH))
            }
            CreateAction.ToggleContextExpanded -> _state.update {
                it.copy(isContextExpanded = !it.isContextExpanded)
            }
            CreateAction.ResetToDefaults -> reset()
            CreateAction.Submit -> submit()
        }
    }

    private fun reset() {
        viewModelScope.launch {
            val prefs = observePreferences().first()
            _state.update {
                CreateState(
                    useCase = UseCase.fromKey(prefs.defaultUseCase),
                    recipient = Recipient.fromName(prefs.defaultRecipient),
                    customRecipientChoices = prefs.customRecipients,
                    tone = Tone.fromKey(prefs.defaultTone),
                    length = Length.fromKey(prefs.defaultLength),
                    isPlusUser = prefs.isPlusSubscriber,
                    remainingFreeToday = it.remainingFreeToday,
                    isPrefilled = true
                )
            }
        }
    }

    private fun submit() {
        val snapshot = _state.value
        if (!snapshot.isValid) return
        if (snapshot.showOutOfQuota) {
            viewModelScope.launch { _events.send(CreateEvent.ShowPaywall) }
            return
        }
        _state.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            // Compose userContext from the new freeform fields. Each part is on its own line
            // so the LLM can parse them as separate facts even though they share one slot.
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
            }
            val request = GenerationRequest(
                useCase = snapshot.useCase.storageKey,
                recipient = snapshot.recipient.displayName,
                recipientIsCustom = snapshot.recipient.isCustom,
                needs = snapshot.needs.map { it.storageKey },
                tone = snapshot.tone.storageKey,
                length = snapshot.length.storageKey,
                userContext = builtContext.takeIf { it.isNotBlank() }
            )
            val result = generatePrayerUseCase(request)
            resultHolder.put(request, result)
            _state.update { it.copy(isSubmitting = false) }

            when (result) {
                is GenerationResult.Success, is GenerationResult.Fallback ->
                    _events.send(CreateEvent.NavigateToLoading)
                is GenerationResult.SoftBlocked ->
                    _events.send(CreateEvent.NavigateToLoading)
                is GenerationResult.RateLimited ->
                    _events.send(CreateEvent.ShowPaywall)
                is GenerationResult.Error ->
                    _events.send(CreateEvent.ShowError(result.userMessage))
            }
        }
    }
}
