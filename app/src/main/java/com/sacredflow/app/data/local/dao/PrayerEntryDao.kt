package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sacredflow.app.data.local.entity.PrayerEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerEntryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: PrayerEntry): Long

    @Update
    suspend fun update(entry: PrayerEntry)

    @Query("UPDATE prayer_entries SET userNote = :note, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNote(id: Long, note: String?, updatedAt: Long)

    @Query("UPDATE prayer_entries SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Long)

    @Query("UPDATE prayer_entries SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE prayer_entries SET isDeleted = 0, updatedAt = :now WHERE id = :id")
    suspend fun undoSoftDelete(id: Long, now: Long)

    @Query("DELETE FROM prayer_entries WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM prayer_entries WHERE isDeleted = 1 AND updatedAt < :before")
    suspend fun purgeOldSoftDeletes(before: Long): Int

    @Query("SELECT * FROM prayer_entries WHERE id = :id")
    fun observeById(id: Long): Flow<PrayerEntry?>

    @Query("SELECT * FROM prayer_entries WHERE id = :id")
    suspend fun getById(id: Long): PrayerEntry?

    @Query("""
        SELECT * FROM prayer_entries 
        WHERE isDeleted = 0 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun observeRecent(limit: Int): Flow<List<PrayerEntry>>

    @Query("""
        SELECT * FROM prayer_entries 
        WHERE isDeleted = 0 
        ORDER BY createdAt DESC
    """)
    fun observeAll(): Flow<List<PrayerEntry>>

    @Query("""
        SELECT * FROM prayer_entries 
        WHERE isDeleted = 0 AND useCase = :useCase 
        ORDER BY createdAt DESC
    """)
    fun observeByUseCase(useCase: String): Flow<List<PrayerEntry>>

    @Query("""
        SELECT pe.* FROM prayer_entries pe
        INNER JOIN favorites f ON f.prayerEntryId = pe.id
        WHERE pe.isDeleted = 0
        ORDER BY f.createdAt DESC
    """)
    fun observeFavorites(): Flow<List<PrayerEntry>>

    @Query("""
        SELECT * FROM prayer_entries 
        WHERE isDeleted = 0 
          AND (bodyText LIKE '%' || :query || '%' 
            OR userNote LIKE '%' || :query || '%'
            OR userContext LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun search(query: String): Flow<List<PrayerEntry>>

    @Query("SELECT COUNT(*) FROM prayer_entries WHERE isDeleted = 0")
    fun observeCount(): Flow<Int>
}
