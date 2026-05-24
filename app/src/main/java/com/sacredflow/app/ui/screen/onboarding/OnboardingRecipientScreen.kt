package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.DenseChip
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingRecipientScreen(
    navController: NavController,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 1 of 4") }
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
                    text = "Who or what do you address?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "This is just for you. There's no wrong answer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )
                Spacer(modifier = Modifier.height(36.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Recipient.BuiltInOptions.forEach { option ->
                        val selected = state.recipient is Recipient.BuiltIn &&
                                (state.recipient as Recipient.BuiltIn).displayName == option.displayName
                        DenseChip(
                            label = option.displayName,
                            selected = selected,
                            onClick = { viewModel.onAction(OnboardingAction.SetRecipient(option)) }
                        )
                    }
                    DenseChip(
                        label = "Custom…",
                        selected = state.isCustomRecipientMode,
                        onClick = { viewModel.onAction(OnboardingAction.EnableCustomRecipientMode(true)) }
                    )
                }

                AnimatedVisibility(visible = state.isCustomRecipientMode) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value = state.customRecipientDraft,
                            onValueChange = { viewModel.onAction(OnboardingAction.SetCustomRecipientDraft(it)) },
                            placeholder = { Text("e.g., Source, Spirit, Mother Earth") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            PrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = state.canContinueFromRecipient,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}
