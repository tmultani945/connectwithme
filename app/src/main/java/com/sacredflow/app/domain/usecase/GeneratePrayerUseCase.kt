package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.repository.GenerationRepository
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.QuotaStatus
import javax.inject.Inject

class GeneratePrayerUseCase @Inject constructor(
    private val generationRepository: GenerationRepository,
    private val preferenceRepository: PreferenceRepository
) {
    /**
     * @param isDailyAutomatic When true, this is the app-generated daily reflection
     *   (a gift, not a debit). Skips the quota gate AND skips the local increment so
     *   the user keeps their full free allowance for explicit creations.
     */
    suspend operator fun invoke(
        request: GenerationRequest,
        isDailyAutomatic: Boolean = false
    ): GenerationResult {
        preferenceRepository.resetQuotaIfNewDay()
        val prefs = preferenceRepository.get()

        if (!isDailyAutomatic) {
            val quota = QuotaStatus.fromPreference(prefs)
            if (!quota.canGenerate) {
                return GenerationResult.RateLimited(
                    resetAt = quota.resetAt,
                    userMessage = "You've used today's free reflections. Connect Yourself Plus removes the limit."
                )
            }
        }

        val result = generationRepository.generate(request)

        if (!isDailyAutomatic &&
            result is GenerationResult.Success &&
            !prefs.isPlusSubscriber
        ) {
            // Local increment so the UI feels immediate. The server quota is the authority
            // for the next request; we'll converge on it via the response.
            preferenceRepository.incrementFreeGenerationsToday()
        }
        return result
    }
}
