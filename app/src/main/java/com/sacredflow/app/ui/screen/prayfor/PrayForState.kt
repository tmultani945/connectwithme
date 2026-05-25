package com.sacredflow.app.ui.screen.prayfor

import com.sacredflow.app.domain.model.Tone

/**
 * State for the "prayer for someone you love" flow.
 *
 * Distinct from CreateState — it's a stripped-down, single-screen form whose
 * only required input is the target's name. The output is still a normal
 * first-person prayer, but the speaker is interceding for [targetName] (e.g.,
 * "I'm holding Mom today...").
 */
data class PrayForState(
    val targetName: String = "",
    val situation: String = "",
    val tone: Tone = Tone.Gentle,
    val isSubmitting: Boolean = false,
    val isPlusUser: Boolean = false,
    val remainingFreeToday: Int? = null
) {
    val isValid: Boolean
        get() = targetName.trim().isNotEmpty() &&
            targetName.length <= MAX_NAME_LENGTH &&
            situation.length <= MAX_SITUATION_LENGTH

    val showOutOfQuota: Boolean
        get() = !isPlusUser && remainingFreeToday == 0

    companion object {
        const val MAX_NAME_LENGTH = 40
        const val MAX_SITUATION_LENGTH = 200
    }
}

sealed interface PrayForAction {
    data class SetTargetName(val value: String) : PrayForAction
    data class SetSituation(val value: String) : PrayForAction
    data class SetTone(val value: Tone) : PrayForAction
    data object Submit : PrayForAction
}

sealed interface PrayForEvent {
    data object NavigateToLoading : PrayForEvent
    data object ShowPaywall : PrayForEvent
    data class ShowError(val message: String) : PrayForEvent
}
