package com.sacredflow.app.ui.screen.generation

import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared in-memory pipe between any screen that triggers a generation
 * (Onboarding, Create, regenerate from Result) and the Result screen.
 *
 * Lives as a Hilt singleton because the producing screen and the consumer
 * sit on different sides of nav-graph transitions and we don't want to
 * pass large prayer text through SavedStateHandle.
 */
@Singleton
class GenerationResultHolder @Inject constructor() {

    private val _current = MutableStateFlow<Snapshot?>(null)
    val current: StateFlow<Snapshot?> = _current.asStateFlow()

    fun put(
        request: GenerationRequest,
        result: GenerationResult,
        savedPrayerId: Long? = null
    ) {
        _current.value = Snapshot(request, result, savedPrayerId)
    }

    fun clear() {
        _current.value = null
    }

    /**
     * @param savedPrayerId Non-null when the producing screen has already persisted
     *   this prayer (e.g. onboarding auto-saves the first reflection so Home's daily
     *   card can adopt it without spending a second API call).
     */
    data class Snapshot(
        val request: GenerationRequest,
        val result: GenerationResult,
        val savedPrayerId: Long? = null
    )
}
