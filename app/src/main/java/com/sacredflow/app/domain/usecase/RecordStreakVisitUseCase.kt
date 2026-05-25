package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.entity.UserPreference
import com.sacredflow.app.data.repository.PreferenceRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Records that the user "returned" today and updates the streak fields.
 *
 * Streak with grace:
 *  - Same-day re-open: no change.
 *  - Yesterday → today: streak + 1.
 *  - Missed N days (N ≥ 1): if N ≤ remaining grace this month, consume that
 *    many grace days and continue. Otherwise reset to 1.
 *  - Grace budget resets at the start of each calendar month
 *    ([UserPreference.STREAK_GRACE_PER_MONTH] skip-days/month).
 *
 * Idempotent: calling this many times on the same local day is a no-op after
 * the first call. Returns the resulting streak count so callers can surface it.
 */
class RecordStreakVisitUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(): Int {
        val now = clock.nowMillis()
        val todayKey = localDateKey(now)
        val monthKey = localMonthKey(now)

        val prefs = preferenceRepository.get()

        // First visit ever — start the streak at 1.
        if (prefs.streakLastVisitDate == null) {
            preferenceRepository.update {
                it.copy(
                    streakCount = 1,
                    streakLastVisitDate = todayKey,
                    streakGraceMonth = monthKey,
                    streakGraceUsedThisMonth = 0
                )
            }
            return 1
        }

        // Same calendar day — already counted.
        if (prefs.streakLastVisitDate == todayKey) {
            return prefs.streakCount
        }

        val missedDays = daysBetween(prefs.streakLastVisitDate, todayKey) - 1

        // Reset grace counter if we've rolled into a new month.
        val (graceMonth, graceUsed) = if (prefs.streakGraceMonth == monthKey) {
            monthKey to prefs.streakGraceUsedThisMonth
        } else {
            monthKey to 0
        }

        return if (missedDays <= 0) {
            // Consecutive day — streak grows.
            val nextCount = prefs.streakCount + 1
            preferenceRepository.update {
                it.copy(
                    streakCount = nextCount,
                    streakLastVisitDate = todayKey,
                    streakGraceMonth = graceMonth,
                    streakGraceUsedThisMonth = graceUsed
                )
            }
            nextCount
        } else {
            val graceAvailable = UserPreference.STREAK_GRACE_PER_MONTH - graceUsed
            if (missedDays <= graceAvailable) {
                // Within grace — streak survives, grace consumed.
                val nextCount = prefs.streakCount + 1
                preferenceRepository.update {
                    it.copy(
                        streakCount = nextCount,
                        streakLastVisitDate = todayKey,
                        streakGraceMonth = graceMonth,
                        streakGraceUsedThisMonth = graceUsed + missedDays
                    )
                }
                nextCount
            } else {
                // Beyond grace — streak resets to 1.
                preferenceRepository.update {
                    it.copy(
                        streakCount = 1,
                        streakLastVisitDate = todayKey,
                        streakGraceMonth = monthKey,
                        streakGraceUsedThisMonth = 0
                    )
                }
                1
            }
        }
    }

    private fun localDateKey(ms: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }.format(Date(ms))

    private fun localMonthKey(ms: Long): String =
        SimpleDateFormat("yyyy-MM", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }.format(Date(ms))

    /**
     * Number of calendar days between two yyyy-MM-dd strings (b - a).
     * Returns 0 if same day, 1 if consecutive, etc.
     */
    private fun daysBetween(aKey: String, bKey: String): Int {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        val a = fmt.parse(aKey) ?: return 1
        val b = fmt.parse(bKey) ?: return 1
        // Use Calendar for safe DST-aware day arithmetic.
        val calA = Calendar.getInstance(TimeZone.getDefault()).apply {
            time = a; clearTimeOfDay()
        }
        val calB = Calendar.getInstance(TimeZone.getDefault()).apply {
            time = b; clearTimeOfDay()
        }
        val diffMs = calB.timeInMillis - calA.timeInMillis
        return (TimeUnit.MILLISECONDS.toDays(diffMs)).toInt()
    }

    private fun Calendar.clearTimeOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
