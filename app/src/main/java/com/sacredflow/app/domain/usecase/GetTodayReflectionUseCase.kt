package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.dailyreflection.DailyTheme
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Returns today's daily reflection. If the user hasn't received one yet today,
 * generates a fresh one using their stored defaults + today's [DailyTheme],
 * saves it to the library, and marks it as today's daily in preferences.
 *
 * Daily reflections are FREE — they don't decrement the user's quota
 * (see [GeneratePrayerUseCase.invoke] with isDailyAutomatic = true).
 */
class GetTodayReflectionUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val prayerRepository: PrayerRepository,
    private val generatePrayer: GeneratePrayerUseCase,
    private val savePrayer: SavePrayerUseCase,
    private val clock: Clock
) {

    sealed interface Result {
        data class Ready(val prayer: PrayerEntry) : Result
        data class Fallback(val text: String) : Result
        data class Failed(val message: String) : Result
    }

    suspend operator fun invoke(): Result {
        val nowMs = clock.nowMillis()
        val todayKey = localDateKey(nowMs)
        val prefs = preferenceRepository.get()

        // Already have today's daily? Hand it back.
        val existingId = prefs.dailyReflectionPrayerId
        if (prefs.dailyReflectionDate == todayKey && existingId != null) {
            prayerRepository.getById(existingId)?.let { if (!it.isDeleted) return Result.Ready(it) }
        }

        // Adopt a recently-saved prayer (e.g. the just-completed onboarding generation)
        // so we don't double-spend an API call right after the user already received one.
        adoptRecentlySaved(nowMs)?.let { adopted ->
            preferenceRepository.setDailyReflection(todayKey, adopted.id)
            return Result.Ready(adopted)
        }

        return generateNew(prefs, nowMs, todayKey)
    }

    private suspend fun adoptRecentlySaved(nowMs: Long): PrayerEntry? {
        val startOfToday = startOfLocalDay(nowMs)
        // Most recent saved-today wins. If nothing today, do nothing — we generate.
        return prayerRepository.findFirstSince(startOfToday)
    }

    private suspend fun generateNew(
        prefs: com.sacredflow.app.data.local.entity.UserPreference,
        nowMs: Long,
        todayKey: String
    ): Result {
        val theme = DailyTheme.forDate(nowMs)
        val recipient = Recipient.fromName(prefs.defaultRecipient)
        val nameLine = prefs.userName.trim()
            .takeIf { it.isNotEmpty() }
            ?.let { "My name is $it." }
        val context = listOfNotNull(
            nameLine,
            "This is about: ${theme.topic}",
            "A brief reflection to start the day. Address me by name if it's given."
        ).joinToString("\n")

        val request = GenerationRequest(
            useCase = UseCase.Reflection.storageKey,
            recipient = recipient.displayName,
            recipientIsCustom = recipient.isCustom,
            needs = emptyList(),
            tone = (prefs.defaultTone.takeIf { it.isNotBlank() } ?: theme.tone.storageKey),
            length = Length.Short.storageKey,
            userContext = context
        )

        return when (val result = generatePrayer(request, isDailyAutomatic = true)) {
            is GenerationResult.Success -> {
                val id = savePrayer(request, result)
                preferenceRepository.setDailyReflection(todayKey, id)
                prayerRepository.getById(id)?.let { Result.Ready(it) }
                    ?: Result.Failed("Could not load today's reflection.")
            }
            is GenerationResult.Fallback -> Result.Fallback(result.text)
            is GenerationResult.SoftBlocked -> Result.Failed(result.userMessage)
            is GenerationResult.RateLimited -> Result.Failed(result.userMessage)
            is GenerationResult.Error -> Result.Failed(result.userMessage)
        }
    }

    private fun localDateKey(ms: Long): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        return fmt.format(Date(ms))
    }

    private fun startOfLocalDay(ms: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = ms
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
