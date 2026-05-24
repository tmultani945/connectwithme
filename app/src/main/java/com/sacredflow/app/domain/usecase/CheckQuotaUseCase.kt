package com.sacredflow.app.domain.usecase

import com.sacredflow.app.billing.BillingState
import com.sacredflow.app.data.repository.BillingRepository
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.domain.model.QuotaStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class CheckQuotaUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val billingRepository: BillingRepository
) {
    fun observe(): Flow<QuotaStatus> =
        combine(
            preferenceRepository.observe(),
            billingRepository.state
        ) { prefs, billing ->
            val billingSubscribed = (billing as? BillingState.Ready)?.isSubscribed == true
            QuotaStatus.fromPreference(prefs).copy(
                isPlusSubscriber = prefs.isPlusSubscriber || billingSubscribed
            )
        }

    suspend operator fun invoke(): QuotaStatus {
        preferenceRepository.resetQuotaIfNewDay()
        return QuotaStatus.fromPreference(preferenceRepository.get())
    }
}
