package com.sacredflow.app.data.repository

import android.app.Activity
import com.sacredflow.app.billing.BillingManager
import com.sacredflow.app.billing.BillingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayBillingRepository @Inject constructor(
    private val billingManager: BillingManager,
    private val preferenceRepository: PreferenceRepository
) : BillingRepository {

    init {
        billingManager.start()
        // Mirror subscription state back into UserPreference for offline reads.
        CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            billingManager.state.collect { state ->
                if (state is BillingState.Ready) {
                    preferenceRepository.setSubscriberStatus(state.isSubscribed)
                }
            }
        }
    }

    override val state: Flow<BillingState> = billingManager.state

    override suspend fun refresh() = billingManager.refresh()

    override suspend fun launchPurchase(activity: Activity, productId: String) {
        billingManager.launchPurchase(activity, productId)
    }

    override suspend fun restorePurchases() = billingManager.refresh()
}
