package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.repository.PrayerRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val analytics: Analytics
) {
    suspend operator fun invoke(prayerId: Long): Boolean {
        val nowFavorited = prayerRepository.toggleFavorite(prayerId)
        if (nowFavorited) analytics.log(AnalyticsEvent.PrayerFavorited)
        return nowFavorited
    }
}
