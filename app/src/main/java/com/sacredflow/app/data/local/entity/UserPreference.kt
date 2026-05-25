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
    val isPlusSubscriber: Boolean = false,
    // Daily reflection — local-date string (yyyy-MM-dd) + prayer id for today's
    // auto-generated reflection. Null until first daily is produced.
    val dailyReflectionDate: String? = null,
    val dailyReflectionPrayerId: Long? = null,
    // Name + freeform context captured during onboarding so the daily generator
    // can address the user by name and stay loosely thematic without re-asking.
    val userName: String = "",
    // Streak with grace — "you've returned N days." Resilient to one or two
    // missed days per month so the user doesn't dread breaking it.
    val streakCount: Int = 0,
    val streakLastVisitDate: String? = null,   // yyyy-MM-dd
    val streakGraceMonth: String? = null,      // yyyy-MM (rolls every calendar month)
    val streakGraceUsedThisMonth: Int = 0,
    // OpenAI TTS voice for "Speak after me." Storage key matches the OpenAI
    // voice id; default Nova is the free, warm-feminine option.
    val voiceKey: String = "nova"
) {
    companion object {
        const val SINGLETON_ID = 1
        const val FREE_DAILY_QUOTA = 5
        /** Skip-days the user gets per calendar month before a streak breaks. */
        const val STREAK_GRACE_PER_MONTH = 2
    }
}
