package com.sacredflow.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.domain.usecase.CheckQuotaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val checkQuotaUseCase: CheckQuotaUseCase,
    private val clock: Clock
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        prayerRepository.observeRecent(limit = 5),
        prayerRepository.observeFavoriteIds(),
        checkQuotaUseCase.observe()
    ) { recents, favIds, quota ->
        HomeState(
            greeting = greetingFor(clock.nowMillis()),
            recents = recents,
            favoriteIds = favIds,
            isPlusUser = quota.isPlusSubscriber,
            remainingFreeToday = quota.remainingToday,
            totalFreeDaily = quota.freeGenerationsTotal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeState(greeting = greetingFor(clock.nowMillis()))
    )

    private fun greetingFor(nowMs: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMs }
        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning."
            in 12..16 -> "Good afternoon."
            in 17..20 -> "Good evening."
            else -> "Hello, friend."
        }
    }
}
