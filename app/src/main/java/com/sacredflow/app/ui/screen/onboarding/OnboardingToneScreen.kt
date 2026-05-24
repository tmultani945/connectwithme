package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.ToneCard
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.util.iconRes

@Composable
fun OnboardingToneScreen(
    navController: NavController,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 3 of 4") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    Text(
                        text = "How would you like it to feel?",
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The mood of your reflection.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.ink2
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Tone.entries) { tone ->
                        ToneCard(
                            name = tone.displayName,
                            descriptor = tone.descriptor,
                            sampleLine = tone.sampleLine,
                            selected = state.tone == tone,
                            onClick = { viewModel.onAction(OnboardingAction.SetTone(tone)) },
                            iconRes = tone.iconRes()
                        )
                    }
                }
            }

            PrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = state.canContinueFromTone,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            )
        }
    }
}
