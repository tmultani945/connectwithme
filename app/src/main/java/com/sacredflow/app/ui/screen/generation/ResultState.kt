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
    val noActiveResult: Boolean = false
)

sealed interface ResultEvent {
    data class CopiedToClipboard(val text: String) : ResultEvent
    data class ShareText(val text: String) : ResultEvent
    data object NavigateHome : ResultEvent
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : ResultEvent
}
