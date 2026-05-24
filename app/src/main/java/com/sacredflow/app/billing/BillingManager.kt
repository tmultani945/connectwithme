package com.sacredflow.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client: BillingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .setListener(this)
        .build()

    private val _state = MutableStateFlow<BillingState>(BillingState.Loading)
    val state: StateFlow<BillingState> = _state.asStateFlow()

    private var connectionDeferred: CompletableDeferred<Boolean>? = null

    fun start() {
        scope.launch { refresh() }
    }

    suspend fun refresh() {
        if (!ensureConnected()) {
            _state.value = BillingState.Error("Couldn't connect to Play Billing.")
            return
        }
        val products = queryProductDetails()
        val purchases = querySubscriptionPurchases()
        val subscribedProductId = purchases
            .firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            ?.products?.firstOrNull()

        // Acknowledge any unacknowledged purchases.
        purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .forEach { acknowledge(it) }

        _state.value = BillingState.Ready(
            isSubscribed = subscribedProductId != null,
            monthlyProduct = products.firstOrNull {
                it.productId == BillingProducts.SACRED_FLOW_PLUS_MONTHLY
            }?.toProduct(),
            yearlyProduct = products.firstOrNull {
                it.productId == BillingProducts.SACRED_FLOW_PLUS_YEARLY
            }?.toProduct(),
            activeProductId = subscribedProductId
        )
    }

    suspend fun launchPurchase(activity: Activity, productId: String) {
        if (!ensureConnected()) return
        val product = queryProductDetails().firstOrNull { it.productId == productId } ?: return
        val offer = product.subscriptionOfferDetails?.firstOrNull() ?: return

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .setOfferToken(offer.offerToken)
                        .build()
                )
            )
            .build()
        client.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode != BillingClient.BillingResponseCode.OK) return
        purchases?.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                scope.launch { acknowledge(purchase) }
            }
        }
        scope.launch { refresh() }
    }

    private suspend fun ensureConnected(): Boolean {
        if (client.isReady) return true

        val deferred = connectionDeferred?.takeIf { !it.isCompleted } ?: CompletableDeferred<Boolean>().also {
            connectionDeferred = it
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (!it.isCompleted) it.complete(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                }
                override fun onBillingServiceDisconnected() {
                    if (!it.isCompleted) it.complete(false)
                }
            })
        }
        return try {
            deferred.await()
        } catch (t: Throwable) {
            false
        }
    }

    private suspend fun queryProductDetails(): List<ProductDetails> = suspendCancellableCoroutine { cont ->
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BillingProducts.SACRED_FLOW_PLUS_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BillingProducts.SACRED_FLOW_PLUS_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        client.queryProductDetailsAsync(params) { _, productDetailsList ->
            if (cont.isActive) cont.resume(productDetailsList)
        }
    }

    private suspend fun querySubscriptionPurchases(): List<Purchase> = suspendCancellableCoroutine { cont ->
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        client.queryPurchasesAsync(params) { _, purchases ->
            if (cont.isActive) cont.resume(purchases)
        }
    }

    private suspend fun acknowledge(purchase: Purchase): Boolean = suspendCancellableCoroutine { cont ->
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        client.acknowledgePurchase(params) { result ->
            if (cont.isActive) cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
        }
    }

    private fun ProductDetails.toProduct(): Product {
        val pricing = subscriptionOfferDetails?.firstOrNull()
            ?.pricingPhases?.pricingPhaseList?.firstOrNull()
        return Product(
            productId = productId,
            title = title,
            formattedPrice = pricing?.formattedPrice ?: "—",
            pricePerPeriodLabel = pricing?.billingPeriod ?: ""
        )
    }
}
