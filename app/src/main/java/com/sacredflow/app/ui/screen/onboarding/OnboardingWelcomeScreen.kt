package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

@Composable
fun OnboardingWelcomeScreen(
    onNext: () -> Unit
) {
    val palette = LocalSacredPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF4D9C0).copy(alpha = 0.55f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .sacredPaper(density = 0.5f)
            .padding(horizontal = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Asterism(size = 10.dp)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "A quiet space for what matters to you.",
                style = MaterialTheme.typography.displayLarge,
                color = palette.primaryInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 360.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Connect Yourself helps you put words to your prayers, intentions, and reflections — in whatever spiritual language is yours.",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.ink2,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 360.dp)
            )
        }
        PrimaryButton(
            text = "Begin",
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
