package com.sacredflow.app.billing

sealed interface BillingState {
    data object Loading : BillingState
    data class Ready(
        val isSubscribed: Boolean,
        val monthlyProduct: Product?,
        val yearlyProduct: Product?,
        val activeProductId: String? = null,
        val renewsAt: Long? = null
    ) : BillingState
    data class Error(val message: String) : BillingState
}

data class Product(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val pricePerPeriodLabel: String
)

object BillingProducts {
    const val SACRED_FLOW_PLUS_MONTHLY = "sacred_flow_plus_monthly"
    const val SACRED_FLOW_PLUS_YEARLY = "sacred_flow_plus_yearly"
}
