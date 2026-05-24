package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sacredflow.app.data.local.entity.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorites WHERE prayerEntryId = :prayerId")
    suspend fun removeByPrayerId(prayerId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE prayerEntryId = :prayerId)")
    suspend fun isFavorited(prayerId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE prayerEntryId = :prayerId)")
    fun observeIsFavorited(prayerId: Long): Flow<Boolean>

    @Query("SELECT prayerEntryId FROM favorites")
    fun observeFavoriteIds(): Flow<List<Long>>
}
