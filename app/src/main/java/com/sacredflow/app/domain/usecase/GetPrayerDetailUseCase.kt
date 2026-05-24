package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class PrayerDetail(
    val entry: PrayerEntry,
    val isFavorited: Boolean
)

class GetPrayerDetailUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository
) {
    operator fun invoke(prayerId: Long): Flow<PrayerDetail?> =
        combine(
            prayerRepository.observeById(prayerId),
            prayerRepository.observeIsFavorited(prayerId)
        ) { entry, isFav ->
            entry?.let { PrayerDetail(it, isFav) }
        }

    suspend fun markViewed(prayerId: Long) {
        prayerRepository.incrementViewCount(prayerId)
    }

    suspend fun updateNote(prayerId: Long, note: String?) {
        prayerRepository.updateNote(prayerId, note)
    }
}
