package com.sacredflow.app.ui.screen.paywall

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.billing.BillingProducts
import com.sacredflow.app.billing.Product
import com.sacredflow.app.data.repository.BillingRepository
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(
    onClose: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val billingRepository = remember { (context.applicationContext as PaywallBillingHost).billingRepository() }
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PaywallEvent.LaunchPurchaseFlow -> {
                    val activity = context as? Activity
                    if (activity != null) {
                        coroutineScope.launch {
                            billingRepository.launchPurchase(activity, event.productId)
                            viewModel.onPurchaseLaunched()
                        }
                    }
                }
                is PaywallEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(state.isSubscribed) {
        if (state.isSubscribed) {
            viewModel.logPurchaseSuccess()
            snackbarHostState.showSnackbar("Welcome to Connect Yourself Plus.")
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF6EBD6).copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .sacredPaper(density = 0.5f)
        ) {
            // ── Top bar — just the close X, always tappable, never scrolled past ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = palette.ink2)
                }
            }

            // ── Scrollable body ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Asterism(size = 10.dp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Make the practice yours.",
                    style = MaterialTheme.typography.displayLarge,
                    color = palette.primaryInk
                )
                Text(
                    text = "Plus deepens what's already here — variety in your daily reflection, length when you need it, voice without limits, and reminders that fit your rhythm.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Frame each line as the thing the user *gets*, not the limit removed.
                // Matches the post-Phase-1 emotional contract: the practice meets you.
                FeatureRow("A daily reflection that varies with you")
                FeatureRow("Unlimited reflections, any time")
                FeatureRow("Long-form prayers, when you want more")
                FeatureRow("Voice readings without limits")
                FeatureRow("Multiple reminders, your way")
                FeatureRow("Priority generation, always")

                Spacer(modifier = Modifier.height(8.dp))

                PlanCard(
                    product = state.yearly,
                    fallbackTitle = "Yearly",
                    fallbackPrice = "$29.99 / year",
                    badge = "Save 50%",
                    selected = state.selectedProductId == BillingProducts.SACRED_FLOW_PLUS_YEARLY,
                    onClick = { viewModel.onAction(PaywallAction.SelectProduct(BillingProducts.SACRED_FLOW_PLUS_YEARLY)) }
                )
                PlanCard(
                    product = state.monthly,
                    fallbackTitle = "Monthly",
                    fallbackPrice = "$4.99 / month",
                    badge = null,
                    selected = state.selectedProductId == BillingProducts.SACRED_FLOW_PLUS_MONTHLY,
                    onClick = { viewModel.onAction(PaywallAction.SelectProduct(BillingProducts.SACRED_FLOW_PLUS_MONTHLY)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Pinned bottom action area — its own slot below the scroll, no overlap ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.92f))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                PrimaryButton(
                    text = if (state.isSubscribed) "Manage subscription" else "Start Connect Yourself Plus",
                    onClick = { viewModel.onAction(PaywallAction.Purchase) },
                    enabled = !state.isPurchaseInFlight && !state.isSubscribed,
                    isLoading = state.isPurchaseInFlight
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { viewModel.onAction(PaywallAction.Restore) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Restore purchases", style = MaterialTheme.typography.labelLarge, color = palette.ink2)
                }
                Text(
                    text = "Cancel anytime. Subscriptions auto-renew.",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.ink3,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(text: String) {
    val palette = LocalSacredPalette.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(palette.primarySoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = palette.primaryInk,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 7.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = palette.ink)
    }
}

@Composable
private fun PlanCard(
    product: Product?,
    fallbackTitle: String,
    fallbackPrice: String,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalSacredPalette.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) palette.primaryInk else palette.outlineSoft
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product?.title ?: fallbackTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.primaryInk
                )
                if (badge != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Text(
                text = product?.formattedPrice ?: fallbackPrice,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.ink2
            )
        }
    }
}

interface PaywallBillingHost {
    fun billingRepository(): BillingRepository
}
