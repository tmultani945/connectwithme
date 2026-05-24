package com.sacredflow.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sacredflow.app.R

/**
 * The shared app-level backdrop — the same look from the Onboarding Welcome screen,
 * applied behind every screen so the app has one consistent atmosphere.
 *
 * Three layers, bottom to top:
 *   1. Cadillac Mountain sunrise photo at 22% opacity, top-aligned crop
 *      (drama in the sky reads near the top of every screen).
 *   2. Warm vertical gradient — apricot at top fading to linen — at moderate
 *      opacity so screen content reads clearly while the photo whispers through.
 *
 * Screen-level `sacredPaper()` modifiers still apply on top of this. The combined
 * effect is photo + gradient + paper grain on every page.
 *
 * Use at the app root, below the NavHost.
 */
@Composable
fun SacredAppBackdrop(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.welcome_backdrop),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            alpha = 0.22f,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF4D9C0).copy(alpha = 0.30f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.62f)
                        )
                    )
                )
        )
    }
}
