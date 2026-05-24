package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sacredflow.app.domain.model.UseCase
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@Composable
fun OnboardingUseCaseScreen(
    navController: NavController,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 1 of 6") }
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
                    text = "What brings you here today?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "You can change this anytime.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )
                Spacer(modifier = Modifier.height(36.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(UseCase.entries) { uc ->
                        UseCaseCard(
                            useCase = uc,
                            selected = state.useCase == uc,
                            onClick = { viewModel.onAction(OnboardingAction.SetUseCase(uc)) }
                        )
                    }
                }
            }
            PrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = state.canContinueFromUseCase,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun UseCaseCard(
    useCase: UseCase,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalSacredPalette.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            if (selected) palette.primaryInk.copy(alpha = 0.5f) else palette.outlineSoft
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = useCase.displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = palette.primaryInk
            )
            Text(
                text = useCase.description,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.ink2
            )
        }
    }
}
