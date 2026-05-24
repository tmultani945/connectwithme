package com.sacredflow.app.ui.screen.create

import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase

data class CreateState(
    val useCase: UseCase = UseCase.Reflection,
    val recipient: Recipient = Recipient.BuiltIn("Universe"),
    val customRecipientDraft: String = "",
    val isCustomRecipientMode: Boolean = false,
    val customRecipientChoices: List<String> = emptyList(),
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
    val isValid: Boolean
        get() {
            val recipientOk = if (isCustomRecipientMode) customRecipientDraft.isNotBlank()
            else true
            return needs.isNotEmpty() && userContext.length <= MAX_CONTEXT_LENGTH && recipientOk
        }

    val canSelectLong: Boolean get() = isPlusUser

    val showOutOfQuota: Boolean
        get() = !isPlusUser && remainingFreeToday == 0

    companion object {
        const val MAX_CONTEXT_LENGTH = 280
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
