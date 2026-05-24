package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.ChipCloud
import com.sacredflow.app.ui.components.ChipItem
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@Composable
fun OnboardingNeedScreen(
    navController: NavController,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 3 of 6") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
                .padding(horizontal = 28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "What would you like to bring in?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Pick one or two.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )
                Spacer(modifier = Modifier.height(36.dp))

                ChipCloud(
                    items = Need.entries.map { need ->
                        ChipItem(
                            value = need,
                            label = need.displayName,
                            enabled = need in state.needs || state.needs.size < Need.MAX_SELECTABLE
                        )
                    },
                    selected = state.needs,
                    onToggle = { viewModel.onAction(OnboardingAction.ToggleNeed(it)) }
                )

                AnimatedVisibility(visible = state.needs.isNotEmpty()) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pick up to ${Need.MAX_SELECTABLE}.",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.ink3
                        )
                    }
                }
            }

            PrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = state.canContinueFromNeed,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}
