package com.sacredflow.app.data.repository

import android.app.Activity
import com.sacredflow.app.billing.BillingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub used until Batch 11 wires the real Play Billing impl.
 * Reports an un-subscribed user, no-ops purchase calls.
 * Lets every screen that depends on BillingRepository compile and run.
 */
@Singleton
class BillingRepositoryStub @Inject constructor() : BillingRepository {

    private val _state = MutableStateFlow<BillingState>(
        BillingState.Ready(
            isSubscribed = false,
            monthlyProduct = null,
            yearlyProduct = null,
            activeProductId = null,
            renewsAt = null
        )
    )

    override val state: Flow<BillingState> = _state.asStateFlow()

    override suspend fun refresh() = Unit

    override suspend fun launchPurchase(activity: Activity, productId: String) = Unit

    override suspend fun restorePurchases() = Unit
}
