package com.sacredflow.app.data.repository

import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.dao.FavoriteDao
import com.sacredflow.app.data.local.dao.GenerationHistoryDao
import com.sacredflow.app.data.local.dao.PrayerEntryDao
import com.sacredflow.app.data.local.entity.Favorite
import com.sacredflow.app.data.local.entity.PrayerEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerDao: PrayerEntryDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: GenerationHistoryDao,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : PrayerRepository {

    override fun observeRecent(limit: Int): Flow<List<PrayerEntry>> =
        prayerDao.observeRecent(limit)

    override fun observeLibrary(filter: LibraryFilter, query: String): Flow<List<PrayerEntry>> {
        val trimmed = query.trim()
        // If the user is searching, the search query overrides filters except Favorites.
        val baseFlow: Flow<List<PrayerEntry>> = when {
            trimmed.isNotEmpty() && filter == LibraryFilter.Favorites ->
                prayerDao.observeFavorites().map { list ->
                    list.filter { it.matches(trimmed) }
                }
            trimmed.isNotEmpty() ->
                prayerDao.search(trimmed)
            filter == LibraryFilter.All ->
                prayerDao.observeAll()
            filter == LibraryFilter.Favorites ->
                prayerDao.observeFavorites()
            else ->
                prayerDao.observeByUseCase(filter.name)
        }
        return baseFlow
    }

    override fun observeById(id: Long): Flow<PrayerEntry?> = prayerDao.observeById(id)

    override fun observeFavoriteIds(): Flow<Set<Long>> =
        favoriteDao.observeFavoriteIds().map { it.toSet() }

    override fun observeIsFavorited(id: Long): Flow<Boolean> =
        favoriteDao.observeIsFavorited(id)

    override fun observeTotalCount(): Flow<Int> = prayerDao.observeCount()

    override suspend fun save(entry: PrayerEntry, generationHistoryId: Long?): Long =
        withContext(dispatchers.io) {
            val now = clock.nowMillis()
            val toInsert = entry.copy(
                createdAt = if (entry.createdAt == 0L) now else entry.createdAt,
                updatedAt = now
            )
            val newId = prayerDao.insert(toInsert)
            generationHistoryId?.let { historyDao.linkToPrayer(it, newId) }
            newId
        }

    override suspend fun updateNote(id: Long, note: String?) = withContext(dispatchers.io) {
        prayerDao.updateNote(id, note?.takeIf { it.isNotBlank() }, clock.nowMillis())
    }

    override suspend fun toggleFavorite(id: Long): Boolean = withContext(dispatchers.io) {
        val isFav = favoriteDao.isFavorited(id)
        if (isFav) {
            favoriteDao.removeByPrayerId(id)
            false
        } else {
            favoriteDao.insert(
                Favorite(prayerEntryId = id, createdAt = clock.nowMillis())
            )
            true
        }
    }

    override suspend fun softDelete(id: Long) = withContext(dispatchers.io) {
        prayerDao.softDelete(id, clock.nowMillis())
    }

    override suspend fun undoSoftDelete(id: Long) = withContext(dispatchers.io) {
        prayerDao.undoSoftDelete(id, clock.nowMillis())
    }

    override suspend fun purgeExpiredSoftDeletes() = withContext(dispatchers.io) {
        // Anything soft-deleted more than the undo window ago is gone for good.
        prayerDao.purgeOldSoftDeletes(clock.nowMillis() - UNDO_WINDOW_MS)
        Unit
    }

    override suspend fun incrementViewCount(id: Long) = withContext(dispatchers.io) {
        prayerDao.incrementViewCount(id)
    }

    private fun PrayerEntry.matches(query: String): Boolean {
        val q = query.lowercase()
        return bodyText.lowercase().contains(q) ||
                (userNote?.lowercase()?.contains(q) == true) ||
                (userContext?.lowercase()?.contains(q) == true)
    }

    companion object {
        // Slightly longer than the 5s UI window — gives time for a snackbar action.
        const val UNDO_WINDOW_MS = 10_000L
    }
}
