package com.sacredflow.app.ui.screen.create

import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase

data class CreateState(
    val useCase: UseCase = UseCase.Prayer,
    val recipient: Recipient = Recipient.BuiltIn("Universe"),
    val customRecipientDraft: String = "",
    val isCustomRecipientMode: Boolean = false,
    val customRecipientChoices: List<String> = emptyList(),
    // Replaces structured Need selection — a freeform topic the user wants to be addressed.
    val topic: String = "",
    // Optional — folded into the prompt so the AI can address the user by name.
    val userName: String = "",
    // Legacy field kept for backward compat; the new flow doesn't surface a multi-needs picker.
    val needs: Set<Need> = emptySet(),
    val tone: Tone = Tone.Gentle,
    val length: Length = Length.Medium,
    val userContext: String = "",
    val isContextExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val isPlusUser: Boolean = false,
    val remainingFreeToday: Int? = null,
    val isPrefilled: Boolean = false
) {
    // Topic is OPTIONAL — the quick path can submit with no topic; the worker
    // prompt treats topic as a "what the speaker wants to pray about" hint and
    // generates a reasonable general reflection when it's absent.
    val isValid: Boolean
        get() {
            val recipientOk = if (isCustomRecipientMode) customRecipientDraft.isNotBlank() else true
            return recipientOk &&
                topic.length <= MAX_TOPIC_LENGTH &&
                userContext.length <= MAX_CONTEXT_LENGTH &&
                userName.length <= MAX_NAME_LENGTH
        }

    val canSelectLong: Boolean get() = isPlusUser

    val showOutOfQuota: Boolean
        get() = !isPlusUser && remainingFreeToday == 0

    companion object {
        const val MAX_CONTEXT_LENGTH = 280
        const val MAX_TOPIC_LENGTH = 140
        const val MAX_NAME_LENGTH = 40
    }
}

sealed interface CreateAction {
    data class SetUseCase(val value: UseCase) : CreateAction
    data class SetRecipient(val value: Recipient) : CreateAction
    data class EnableCustomRecipientMode(val enabled: Boolean) : CreateAction
    data class SetCustomRecipientDraft(val draft: String) : CreateAction
    data class ToggleNeed(val value: Need) : CreateAction
    data class SetTone(val value: Tone) : CreateAction
    data class SetLength(val value: Length) : CreateAction
    data class SetTopic(val text: String) : CreateAction
    data class SetUserName(val text: String) : CreateAction
    data class SetUserContext(val text: String) : CreateAction
    data object ToggleContextExpanded : CreateAction
    data object Submit : CreateAction
    data object ResetToDefaults : CreateAction
}

sealed interface CreateEvent {
    data object NavigateToLoading : CreateEvent
    data object ShowPaywall : CreateEvent
    data class ShowError(val message: String) : CreateEvent
}
