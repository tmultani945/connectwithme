package com.sacredflow.app.ui.screen.onboarding

import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone

data class OnboardingState(
    val recipient: Recipient? = null,
    val customRecipientDraft: String = "",
    val isCustomRecipientMode: Boolean = false,
    val topic: String = "",
    val tone: Tone? = null,
    val userName: String = "",
    val userContext: String = "",
    val isSubmitting: Boolean = false
) {
    val canContinueFromRecipient: Boolean
        get() = if (isCustomRecipientMode) customRecipientDraft.trim().isNotEmpty()
        else recipient != null && !recipient.isCustom

    val canContinueFromTopic: Boolean
        get() = topic.trim().isNotEmpty() && topic.length <= MAX_TOPIC_LENGTH

    val canContinueFromTone: Boolean get() = tone != null

    val isContextValid: Boolean
        get() = userContext.length <= MAX_CONTEXT_LENGTH &&
            userName.length <= MAX_NAME_LENGTH

    companion object {
        const val MAX_CONTEXT_LENGTH = 280
        const val MAX_TOPIC_LENGTH = 140
        const val MAX_NAME_LENGTH = 40
    }
}

sealed interface OnboardingAction {
    data class SetRecipient(val recipient: Recipient) : OnboardingAction
    data class EnableCustomRecipientMode(val enabled: Boolean) : OnboardingAction
    data class SetCustomRecipientDraft(val draft: String) : OnboardingAction
    data class SetTopic(val text: String) : OnboardingAction
    data class SetTone(val tone: Tone) : OnboardingAction
    data class SetUserName(val text: String) : OnboardingAction
    data class SetUserContext(val text: String) : OnboardingAction
    data object Submit : OnboardingAction
}

sealed interface OnboardingEvent {
    data class NavigateToResult(val text: String, val isFallback: Boolean) : OnboardingEvent
    data object NavigateToCrisisResources : OnboardingEvent
    data class ShowError(val message: String) : OnboardingEvent
}
