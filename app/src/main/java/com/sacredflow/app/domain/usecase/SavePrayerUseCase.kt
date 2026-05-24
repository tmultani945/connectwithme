package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import javax.inject.Inject

class SavePrayerUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val analytics: Analytics,
    private val clock: Clock
) {
    suspend operator fun invoke(
        request: GenerationRequest,
        result: GenerationResult.Success
    ): Long {
        val now = clock.nowMillis()
        val entry = PrayerEntry(
            recipient = request.recipient,
            useCase = request.useCase,
            needs = request.needs,
            tone = request.tone,
            length = request.length,
            bodyText = result.text,
            userContext = request.userContext?.takeIf { it.isNotBlank() },
            userNote = null,
            createdAt = now,
            updatedAt = now,
            viewCount = 0,
            isDeleted = false
        )
        val id = prayerRepository.save(entry, generationHistoryId = result.generationHistoryId)
        analytics.log(AnalyticsEvent.PrayerSaved)
        return id
    }
}
