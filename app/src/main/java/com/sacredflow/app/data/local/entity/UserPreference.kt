package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val defaultRecipient: String = "Universe",
    val defaultUseCase: String = "Reflection",
    val defaultTone: String = "gentle",
    val defaultLength: String = "medium",
    val lastUsedNeeds: List<String> = emptyList(),
    val customRecipients: List<String> = emptyList(),
    val onboardingComplete: Boolean = false,
    val themeMode: String = "system",
    val locale: String = "en-US",
    val freeGenerationsToday: Int = 0,
    val freeGenerationsResetAt: Long = 0L,
    val isPlusSubscriber: Boolean = false
) {
    companion object {
        const val SINGLETON_ID = 1
        const val FREE_DAILY_QUOTA = 5
    }
}
