package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@Composable
fun OnboardingContextScreen(
    navController: NavController,
    onGenerate: () -> Unit,
    onBack: () -> Unit,
    onCrisisResources: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val palette = LocalSacredPalette.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.NavigateToResult -> onGenerate()
                OnboardingEvent.NavigateToCrisisResources -> onCrisisResources()
                is OnboardingEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 5 of 6") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    text = "Anything specific on your mind?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Optional. A few words help personalize what you receive.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )
                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = state.userContext,
                    onValueChange = { viewModel.onAction(OnboardingAction.SetUserContext(it)) },
                    placeholder = {
                        Text(
                            "Starting a new job tomorrow…\nMissing my grandmother…\nHealing after surgery…",
                            color = palette.ink3.copy(alpha = 0.7f)
                        )
                    },
                    minLines = 5,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Stays on your device unless you save it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.ink3
                    )
                    Text(
                        text = "${state.userContext.length} / ${OnboardingState.MAX_CONTEXT_LENGTH}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.ink3
                    )
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)) {
                PrimaryButton(
                    text = "Create my first reflection",
                    onClick = { viewModel.onAction(OnboardingAction.Submit) },
                    enabled = state.isContextValid && !state.isSubmitting,
                    isLoading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = {
                        viewModel.onAction(OnboardingAction.SetUserContext(""))
                        viewModel.onAction(OnboardingAction.Submit)
                    },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Skip", style = MaterialTheme.typography.titleMedium, color = palette.ink3)
                }
            }
        }
    }
}
