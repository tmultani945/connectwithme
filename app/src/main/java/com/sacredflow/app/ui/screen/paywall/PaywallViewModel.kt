package com.sacredflow.app.ui.screen.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacredflow.app.billing.BillingProducts
import com.sacredflow.app.billing.BillingState
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.data.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val _state = MutableStateFlow(
        PaywallState(selectedProductId = BillingProducts.SACRED_FLOW_PLUS_YEARLY)
    )
    val state: StateFlow<PaywallState> = _state.asStateFlow()

    private val _events = Channel<PaywallEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        analytics.log(AnalyticsEvent.PaywallShown)
        viewModelScope.launch {
            billingRepository.state.collect { billing ->
                _state.update { it.copy(billingState = billing) }
            }
        }
        viewModelScope.launch { billingRepository.refresh() }
    }

    fun onAction(action: PaywallAction) {
        when (action) {
            is PaywallAction.SelectProduct -> _state.update {
                it.copy(selectedProductId = action.productId)
            }
            PaywallAction.Purchase -> {
                val id = _state.value.selectedProductId ?: return
                _state.update { it.copy(isPurchaseInFlight = true) }
                viewModelScope.launch {
                    _events.send(PaywallEvent.LaunchPurchaseFlow(id))
                }
            }
            PaywallAction.Restore -> viewModelScope.launch {
                billingRepository.restorePurchases()
                _events.send(PaywallEvent.ShowSnackbar("Checking for past purchases…"))
            }
        }
    }

    fun onPurchaseLaunched() {
        _state.update { it.copy(isPurchaseInFlight = false) }
    }

    fun logPurchaseSuccess() {
        val id = _state.value.selectedProductId ?: return
        analytics.log(AnalyticsEvent.PaywallPurchased(id))
    }
}
