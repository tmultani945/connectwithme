package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.LibraryFilter
import com.sacredflow.app.data.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class LibraryRow(
    val entry: PrayerEntry,
    val isFavorited: Boolean
)

class GetLibraryUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository
) {
    operator fun invoke(filter: LibraryFilter, query: String): Flow<List<LibraryRow>> =
        combine(
            prayerRepository.observeLibrary(filter, query),
            prayerRepository.observeFavoriteIds()
        ) { entries, favIds ->
            entries.map { LibraryRow(it, it.id in favIds) }
        }
}
