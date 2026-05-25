package com.sacredflow.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.repository.PrayerRepository
import com.sacredflow.app.domain.usecase.CheckQuotaUseCase
import com.sacredflow.app.domain.usecase.GetTodayReflectionUseCase
import com.sacredflow.app.domain.usecase.LibraryResurfaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val checkQuotaUseCase: CheckQuotaUseCase,
    private val getTodayReflection: GetTodayReflectionUseCase,
    private val libraryResurface: LibraryResurfaceUseCase,
    private val clock: Clock
) : ViewModel() {

    // Daily + resurface live in their own state so reactive flows (recents, quota)
    // don't reset them while the daily generation is in flight.
    private val _daily = MutableStateFlow<DailyReflectionState>(DailyReflectionState.Loading)
    private val _resurface = MutableStateFlow<List<LibraryResurfaceUseCase.ResurfaceCard>>(emptyList())

    val state: StateFlow<HomeState> = combine(
        // Fetch one extra so we still show 5 cards after filtering out the daily.
        prayerRepository.observeRecent(limit = 6),
        prayerRepository.observeFavoriteIds(),
        checkQuotaUseCase.observe(),
        _daily,
        _resurface
    ) { recents, favIds, quota, daily, resurface ->
        // The daily reflection is rendered as its own hero card up top — don't
        // also surface it in Recent or in the resurface row, or the user sees
        // the same prayer twice (or three times) on the same screen.
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
            resurface = filteredResurface
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeState(greeting = greetingFor(clock.nowMillis()))
    )

    init {
        loadDailyReflection()
        loadResurfaceCards()
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
