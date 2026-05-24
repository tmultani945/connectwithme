package com.sacredflow.app.data.repository

import android.app.Activity
import com.sacredflow.app.billing.BillingState
import kotlinx.coroutines.flow.Flow

interface BillingRepository {

    val state: Flow<BillingState>

    suspend fun refresh()

    suspend fun launchPurchase(activity: Activity, productId: String)

    suspend fun restorePurchases()
}
