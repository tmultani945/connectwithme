package com.sacredflow.app.ui.screen.paywall

import com.sacredflow.app.billing.BillingState
import com.sacredflow.app.billing.Product

data class PaywallState(
    val billingState: BillingState = BillingState.Loading,
    val selectedProductId: String? = null,
    val isPurchaseInFlight: Boolean = false
) {
    val isSubscribed: Boolean
        get() = (billingState as? BillingState.Ready)?.isSubscribed == true

    val monthly: Product?
        get() = (billingState as? BillingState.Ready)?.monthlyProduct

    val yearly: Product?
        get() = (billingState as? BillingState.Ready)?.yearlyProduct
}

sealed interface PaywallAction {
    data class SelectProduct(val productId: String) : PaywallAction
    data object Purchase : PaywallAction
    data object Restore : PaywallAction
}

sealed interface PaywallEvent {
    data class LaunchPurchaseFlow(val productId: String) : PaywallEvent
    data class ShowSnackbar(val message: String) : PaywallEvent
}
