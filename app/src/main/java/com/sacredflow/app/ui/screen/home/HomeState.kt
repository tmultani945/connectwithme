package com.sacredflow.app.ui.screen.home

import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.domain.usecase.LibraryResurfaceUseCase

data class HomeState(
    val greeting: String = "",
    val recents: List<PrayerEntry> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val isPlusUser: Boolean = false,
    val remainingFreeToday: Int = 5,
    val totalFreeDaily: Int = 5,
    val daily: DailyReflectionState = DailyReflectionState.Loading,
    val resurface: List<LibraryResurfaceUseCase.ResurfaceCard> = emptyList(),
    val streakDays: Int = 0
) {
    val quotaLabel: String
        get() = when {
            isPlusUser -> "Connect Yourself Plus"
            remainingFreeToday == 0 -> "Out of free reflections today"
            else -> "$remainingFreeToday of $totalFreeDaily free today"
        }
}

sealed interface DailyReflectionState {
    data object Loading : DailyReflectionState
    data class Ready(val prayer: PrayerEntry) : DailyReflectionState
    data class Fallback(val text: String) : DailyReflectionState
    data class Failed(val message: String) : DailyReflectionState
}
