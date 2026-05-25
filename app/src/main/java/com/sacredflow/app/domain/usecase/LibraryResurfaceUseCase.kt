package com.sacredflow.app.domain.usecase

import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.PrayerRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Surfaces previously saved reflections on Home so the Library stops feeling like a
 * graveyard. Looks for prayers from exactly 7 / 30 / 90 / 365 days ago (±12h window).
 * Returns at most [maxCards] cards.
 */
class LibraryResurfaceUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val clock: Clock
) {

    data class ResurfaceCard(
        val prayer: PrayerEntry,
        val daysAgo: Int,
        val label: String
    )

    suspend operator fun invoke(maxCards: Int = 2): List<ResurfaceCard> {
        val now = clock.nowMillis()
        val windowMs = TimeUnit.HOURS.toMillis(12)
        val results = mutableListOf<ResurfaceCard>()
        val seen = mutableSetOf<Long>()

        for ((days, label) in DAY_BUCKETS) {
            if (results.size >= maxCards) break
            val target = now - TimeUnit.DAYS.toMillis(days.toLong())
            val matches = prayerRepository.findInRange(
                startMs = target - windowMs,
                endMs = target + windowMs,
                limit = 3
            )
            val pick = matches.firstOrNull { it.id !in seen } ?: continue
            seen.add(pick.id)
            results.add(ResurfaceCard(prayer = pick, daysAgo = days, label = label))
        }
        return results
    }

    companion object {
        // (days-ago, label). Ordered nearest-first so a 7-day card always beats a year-old one.
        private val DAY_BUCKETS = listOf(
            7 to "A week ago",
            30 to "A month ago",
            90 to "Three months ago",
            365 to "A year ago"
        )
    }
}
