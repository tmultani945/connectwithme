package com.sacredflow.app.ui.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SplashEvent.NavigateToOnboarding -> onNavigateToOnboarding()
                SplashEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    val palette = LocalSacredPalette.current
    // The global SacredAppBackdrop renders the sunrise photo + warm gradient behind
    // every screen, so this composable just contributes content + paper texture.
    Box(
        modifier = Modifier.fillMaxSize().sacredPaper(density = 0.5f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Asterism(size = 12.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Connect Yourself",
                style = MaterialTheme.typography.displayMedium,
                color = palette.primaryInk
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A QUIET SPACE",
                style = LocalSacredTypography.current.overline,
                color = palette.ink3
            )
        }
    }
}
