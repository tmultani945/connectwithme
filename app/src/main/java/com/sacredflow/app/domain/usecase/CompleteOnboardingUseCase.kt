package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val generatePrayerUseCase: GeneratePrayerUseCase,
    private val analytics: Analytics
) {
    suspend operator fun invoke(input: Input): GenerationResult {
        // Persist the user's onboarding selections as defaults.
        preferenceRepository.update { current ->
            current.copy(
                defaultUseCase = input.useCase.storageKey,
                defaultRecipient = input.recipient.displayName,
                defaultTone = input.tone.storageKey,
                defaultLength = Length.Medium.storageKey,
                lastUsedNeeds = input.needs.map { it.storageKey }
            )
        }
        if (input.recipient is Recipient.Custom) {
            preferenceRepository.addCustomRecipient(input.recipient.displayName)
        }
        preferenceRepository.markOnboardingComplete()
        analytics.log(AnalyticsEvent.OnboardingCompleted)

        // Then run the first generation.
        return generatePrayerUseCase(
            GenerationRequest(
                useCase = input.useCase.storageKey,
                recipient = input.recipient.displayName,
                recipientIsCustom = input.recipient is Recipient.Custom,
                needs = input.needs.map { it.storageKey },
                tone = input.tone.storageKey,
                length = Length.Medium.storageKey,
                userContext = input.userContext?.takeIf { it.isNotBlank() },
                isRegeneration = false
            )
        )
    }

    data class Input(
        val useCase: UseCase,
        val recipient: Recipient,
        val needs: Set<Need>,
        val tone: Tone,
        val userContext: String?
    )
}
