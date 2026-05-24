package com.sacredflow.app.data.repository

import com.sacredflow.app.data.local.entity.PrayerEntry
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {

    fun observeRecent(limit: Int = 5): Flow<List<PrayerEntry>>

    fun observeLibrary(filter: LibraryFilter, query: String): Flow<List<PrayerEntry>>

    fun observeById(id: Long): Flow<PrayerEntry?>

    fun observeFavoriteIds(): Flow<Set<Long>>

    fun observeIsFavorited(id: Long): Flow<Boolean>

    fun observeTotalCount(): Flow<Int>

    suspend fun save(entry: PrayerEntry, generationHistoryId: Long? = null): Long

    suspend fun updateNote(id: Long, note: String?)

    suspend fun toggleFavorite(id: Long): Boolean

    suspend fun softDelete(id: Long)

    suspend fun undoSoftDelete(id: Long)

    suspend fun purgeExpiredSoftDeletes()

    suspend fun incrementViewCount(id: Long)
}

enum class LibraryFilter {
    All,
    Favorites,
    Prayer,
    Intention,
    Gratitude,
    Healing,
    Reflection,
    Custom
}
