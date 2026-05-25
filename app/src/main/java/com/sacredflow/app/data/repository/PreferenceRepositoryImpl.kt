package com.sacredflow.app.data.repository

import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.datastore.AppDataStore
import com.sacredflow.app.data.local.dao.UserPreferenceDao
import com.sacredflow.app.data.local.entity.UserPreference
import com.sacredflow.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val dao: UserPreferenceDao,
    private val dataStore: AppDataStore,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : PreferenceRepository {

    override fun observe(): Flow<UserPreference> = dao.observe().filterNotNull()

    override suspend fun get(): UserPreference = withContext(dispatchers.io) {
        dao.get() ?: UserPreference().also { dao.upsert(it) }
    }

    override suspend fun update(transform: (UserPreference) -> UserPreference) =
        withContext(dispatchers.io) {
            val current = get()
            val next = transform(current)
            dao.upsert(next)
            // Mirror the two fields read at cold start
            if (next.themeMode != current.themeMode) {
                dataStore.setThemeMode(next.themeMode)
            }
            if (next.onboardingComplete != current.onboardingComplete) {
                dataStore.setOnboardingComplete(next.onboardingComplete)
            }
        }

    override suspend fun setThemeMode(mode: ThemeMode) = withContext(dispatchers.io) {
        update { it.copy(themeMode = mode.key) }
    }

    override suspend fun markOnboardingComplete() = withContext(dispatchers.io) {
        dao.markOnboardingComplete()
        dataStore.setOnboardingComplete(true)
    }

    override suspend fun addCustomRecipient(name: String) = withContext(dispatchers.io) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return@withContext
        update { current ->
            if (trimmed in current.customRecipients) current
            else current.copy(customRecipients = current.customRecipients + trimmed)
        }
    }

    override suspend fun incrementFreeGenerationsToday() = withContext(dispatchers.io) {
        resetQuotaIfNewDay()
        dao.incrementFreeGenerationsToday()
    }

    override suspend fun resetQuotaIfNewDay() = withContext(dispatchers.io) {
        val current = get()
        val now = clock.nowMillis()
        if (!isSameLocalDay(current.freeGenerationsResetAt, now)) {
            dao.resetQuota(now)
        }
    }

    override suspend fun setSubscriberStatus(isPlus: Boolean) = withContext(dispatchers.io) {
        dao.setSubscriberStatus(isPlus)
    }

    override suspend fun setDailyReflection(localDate: String, prayerId: Long) =
        withContext(dispatchers.io) {
            dao.setDailyReflection(localDate, prayerId)
        }

    override suspend fun setUserName(name: String) = withContext(dispatchers.io) {
        dao.setUserName(name.trim())
    }

    private fun isSameLocalDay(a: Long, b: Long): Boolean {
        if (a == 0L) return false
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.timeInMillis = a
        val yearA = cal.get(Calendar.YEAR); val dayA = cal.get(Calendar.DAY_OF_YEAR)
        cal.timeInMillis = b
        val yearB = cal.get(Calendar.YEAR); val dayB = cal.get(Calendar.DAY_OF_YEAR)
        return yearA == yearB && dayA == dayB
    }
}
