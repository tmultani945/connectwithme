package com.sacredflow.app.ui.screen.onboarding

import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase

data class OnboardingState(
    val useCase: UseCase? = null,
    val recipient: Recipient? = null,
    val customRecipientDraft: String = "",
    val isCustomRecipientMode: Boolean = false,
    val needs: Set<Need> = emptySet(),
    val tone: Tone? = null,
    val userContext: String = "",
    val isSubmitting: Boolean = false
) {
    val canContinueFromUseCase: Boolean get() = useCase != null
    val canContinueFromRecipient: Boolean
        get() = if (isCustomRecipientMode) customRecipientDraft.trim().isNotEmpty()
        else recipient != null && !recipient.isCustom
    val canContinueFromNeed: Boolean get() = needs.isNotEmpty()
    val canContinueFromTone: Boolean get() = tone != null
    val isContextValid: Boolean get() = userContext.length <= MAX_CONTEXT_LENGTH

    companion object {
        const val MAX_CONTEXT_LENGTH = 280
    }
}

sealed interface OnboardingAction {
    data class SetUseCase(val useCase: UseCase) : OnboardingAction
    data class SetRecipient(val recipient: Recipient) : OnboardingAction
    data class EnableCustomRecipientMode(val enabled: Boolean) : OnboardingAction
    data class SetCustomRecipientDraft(val draft: String) : OnboardingAction
    data class ToggleNeed(val need: Need) : OnboardingAction
    data class SetTone(val tone: Tone) : OnboardingAction
    data class SetUserContext(val text: String) : OnboardingAction
    data object Submit : OnboardingAction
}

sealed interface OnboardingEvent {
    data class NavigateToResult(val text: String, val isFallback: Boolean) : OnboardingEvent
    data object NavigateToCrisisResources : OnboardingEvent
    data class ShowError(val message: String) : OnboardingEvent
}
