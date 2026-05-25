package com.sacredflow.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.domain.model.QuotaStatus
import com.sacredflow.app.domain.usecase.CheckQuotaUseCase
import com.sacredflow.app.domain.usecase.GetTodayReflectionUseCase
import com.sacredflow.app.domain.usecase.LibraryResurfaceUseCase
import com.sacredflow.app.domain.usecase.RecordStreakVisitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val checkQuotaUseCase: CheckQuotaUseCase,
    private val getTodayReflection: GetTodayReflectionUseCase,
    private val libraryResurface: LibraryResurfaceUseCase,
    private val recordStreakVisit: RecordStreakVisitUseCase,
    private val clock: Clock
) : ViewModel() {

    // Async-loaded state that the reactive flows below can read without
    // racing each other.
    private val _daily = MutableStateFlow<DailyReflectionState>(DailyReflectionState.Loading)
    private val _resurface = MutableStateFlow<List<LibraryResurfaceUseCase.ResurfaceCard>>(emptyList())
    private val _streak = MutableStateFlow(0)

    val state: StateFlow<HomeState> = combine(
        // Fetch one extra so we still show 5 cards after filtering out the daily.
        prayerRepository.observeRecent(limit = 6),
        prayerRepository.observeFavoriteIds(),
        checkQuotaUseCase.observe(),
        _daily,
        _resurface,
        _streak
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val recents = values[0] as List<PrayerEntry>
        @Suppress("UNCHECKED_CAST")
        val favIds = values[1] as Set<Long>
        val quota = values[2] as QuotaStatus
        val daily = values[3] as DailyReflectionState
        @Suppress("UNCHECKED_CAST")
        val resurface = values[4] as List<LibraryResurfaceUseCase.ResurfaceCard>
        val streak = values[5] as Int

        // The daily reflection is rendered as its own hero card up top — don't
        // also surface it in Recent or in the resurface row.
        val dailyId = (daily as? DailyReflectionState.Ready)?.prayer?.id
        val filteredRecents = if (dailyId == null) recents.take(5)
            else recents.filter { it.id != dailyId }.take(5)
        val filteredResurface = if (dailyId == null) resurface
            else resurface.filter { it.prayer.id != dailyId }

        HomeState(
            greeting = greetingFor(clock.nowMillis()),
            recents = filteredRecents,
            favoriteIds = favIds,
            isPlusUser = quota.isPlusSubscriber,
            remainingFreeToday = quota.remainingToday,
            totalFreeDaily = quota.freeGenerationsTotal,
            daily = daily,
            resurface = filteredResurface,
            streakDays = streak
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeState(greeting = greetingFor(clock.nowMillis()))
    )

    init {
        loadDailyReflection()
        loadResurfaceCards()
        recordVisit()
    }

    private fun loadDailyReflection() {
        viewModelScope.launch {
            _daily.value = DailyReflectionState.Loading
            _daily.value = when (val r = getTodayReflection()) {
                is GetTodayReflectionUseCase.Result.Ready -> DailyReflectionState.Ready(r.prayer)
                is GetTodayReflectionUseCase.Result.Fallback -> DailyReflectionState.Fallback(r.text)
                is GetTodayReflectionUseCase.Result.Failed -> DailyReflectionState.Failed(r.message)
            }
        }
    }

    private fun loadResurfaceCards() {
        viewModelScope.launch {
            _resurface.value = libraryResurface()
        }
    }

    private fun recordVisit() {
        viewModelScope.launch {
            _streak.value = recordStreakVisit()
        }
    }

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
