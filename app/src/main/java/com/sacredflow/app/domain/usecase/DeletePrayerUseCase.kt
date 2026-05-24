package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.repository.PrayerRepository
import javax.inject.Inject

class DeletePrayerUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val analytics: Analytics
) {
    suspend operator fun invoke(prayerId: Long) {
        prayerRepository.softDelete(prayerId)
        analytics.log(AnalyticsEvent.PrayerDeleted)
    }

    suspend fun undo(prayerId: Long) {
        prayerRepository.undoSoftDelete(prayerId)
    }

    suspend fun purgeExpired() {
        prayerRepository.purgeExpiredSoftDeletes()
    }
}
