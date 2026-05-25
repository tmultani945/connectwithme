package com.sacredflow.app.ui.screen.generation

import com.sacredflow.app.domain.model.GenerationRequest

data class ResultState(
    val request: GenerationRequest? = null,
    val text: String = "",
    val isFallback: Boolean = false,
    val isSaved: Boolean = false,
    val savedPrayerId: Long? = null,
    val generationHistoryId: Long? = null,
    val isRegenerating: Boolean = false,
    val regenCount: Int = 0,
    val isFirstResultPostOnboarding: Boolean = false,
    val noActiveResult: Boolean = false,
    /** Name of the person this prayer is for, when the generation came from
     *  the "Pray for someone you love" flow. Drives FOR [NAME] framing on the
     *  share card and the caption. Null for personal reflections. */
    val prayForTarget: String? = null,
    /** OpenAI TTS voice key for "Speak after me." Loaded from user prefs;
     *  defaults to "nova" until prefs hydrate. */
    val voiceKey: String = "nova",
    /** User's "how did this land?" response storage key (steady/okay/missed). */
    val landed: String? = null
)

sealed interface ResultEvent {
    data class CopiedToClipboard(val text: String) : ResultEvent
    data class ShareText(val text: String) : ResultEvent
    data object NavigateHome : ResultEvent
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : ResultEvent
}
