package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.sacredflow.app.data.local.entity.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Upsert
    suspend fun upsert(preference: UserPreference)

    @Query("SELECT * FROM user_preferences WHERE id = ${UserPreference.SINGLETON_ID}")
    fun observe(): Flow<UserPreference?>

    @Query("SELECT * FROM user_preferences WHERE id = ${UserPreference.SINGLETON_ID}")
    suspend fun get(): UserPreference?

    @Query("""
        UPDATE user_preferences 
        SET freeGenerationsToday = freeGenerationsToday + 1 
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun incrementFreeGenerationsToday()

    @Query("""
        UPDATE user_preferences 
        SET freeGenerationsToday = 0, freeGenerationsResetAt = :resetAt 
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun resetQuota(resetAt: Long)

    @Query("""
        UPDATE user_preferences 
        SET isPlusSubscriber = :isPlus 
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun setSubscriberStatus(isPlus: Boolean)

    @Query("""
        UPDATE user_preferences
        SET onboardingComplete = 1
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun markOnboardingComplete()

    @Query("""
        UPDATE user_preferences
        SET dailyReflectionDate = :date, dailyReflectionPrayerId = :prayerId
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun setDailyReflection(date: String, prayerId: Long)

    @Query("""
        UPDATE user_preferences
        SET userName = :name
        WHERE id = ${UserPreference.SINGLETON_ID}
    """)
    suspend fun setUserName(name: String)
}
