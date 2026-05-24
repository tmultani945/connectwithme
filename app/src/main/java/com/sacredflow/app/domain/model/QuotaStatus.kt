package com.sacredflow.app.domain.model

import com.sacredflow.app.data.local.entity.UserPreference

data class QuotaStatus(
    val isPlusSubscriber: Boolean,
    val freeGenerationsUsedToday: Int,
    val freeGenerationsTotal: Int,
    val resetAt: Long
) {
    // Free trial is unlimited for now — revert these two getters to restore daily-quota gating.
    val remainingToday: Int
        get() = freeGenerationsTotal

    val canGenerate: Boolean
        get() = true

    companion object {
        fun fromPreference(prefs: UserPreference): QuotaStatus = QuotaStatus(
            isPlusSubscriber = prefs.isPlusSubscriber,
            freeGenerationsUsedToday = prefs.freeGenerationsToday,
            freeGenerationsTotal = UserPreference.FREE_DAILY_QUOTA,
            resetAt = prefs.freeGenerationsResetAt
        )
    }
}
