package com.sacredflow.app.ui.screen.home

import com.sacredflow.app.data.local.entity.PrayerEntry

data class HomeState(
    val greeting: String = "",
    val recents: List<PrayerEntry> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val isPlusUser: Boolean = false,
    val remainingFreeToday: Int = 5,
    val totalFreeDaily: Int = 5
) {
    val quotaLabel: String
        get() = when {
            isPlusUser -> "Connect Yourself Plus"
            remainingFreeToday == 0 -> "Out of free reflections today"
            else -> "$remainingFreeToday of $totalFreeDaily free today"
        }
}
