package com.sacredflow.app.ui.screen.generation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sacredflow.app.domain.model.GenerationResult
import com.sacredflow.app.ui.components.BreathingCircle
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.components.sacredVignette
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import kotlinx.coroutines.delay

private val microcopyLines = listOf(
    "choosing words\nfor what you brought.",
    "listening to\nthe quiet beneath.",
    "almost ready —\na few more breaths."
)

private const val MIN_DISPLAY_MS = 2_000L
private const val MICROCOPY_INTERVAL_MS = 2_400L

@Composable
fun GenerationLoadingScreen(
    onResultReady: () -> Unit,
    onCrisisResources: () -> Unit,
    onPaywall: () -> Unit,
    viewModel: GenerationLoadingViewModel = hiltViewModel()
) {
    val snapshot by viewModel.holder.current.collectAsState()
    var microcopyIndex by remember { mutableIntStateOf(0) }
    val startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        while (true) {
            delay(MICROCOPY_INTERVAL_MS)
            microcopyIndex = (microcopyIndex + 1) % microcopyLines.size
        }
    }

    LaunchedEffect(snapshot) {
        val current = snapshot ?: return@LaunchedEffect
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < MIN_DISPLAY_MS) delay(MIN_DISPLAY_MS - elapsed)

        when (current.result) {
            is GenerationResult.Success, is GenerationResult.Fallback -> onResultReady()
            is GenerationResult.SoftBlocked -> onCrisisResources()
            is GenerationResult.RateLimited -> onPaywall()
            is GenerationResult.Error -> onResultReady()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF4E5CC).copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 800f
                )
            )
            .sacredPaper(density = 0.7f)
            .sacredVignette(strength = 0.10f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BreathingCircle(size = 220.dp)
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                text = "LISTENING",
                style = typo.overline,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(18.dp))
            AnimatedContent(
                targetState = microcopyIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "microcopy"
            ) { index ->
                Text(
                    text = microcopyLines[index],
                    style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                    color = palette.primaryInk,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            ThreeDotPulse(color = palette.ink3)
        }
        // "~6 seconds" hint at the bottom.
        Text(
            text = "~6 SECONDS",
            style = typo.overline,
            color = palette.ink3,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}

@Composable
private fun ThreeDotPulse(color: Color) {
    val transition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 0..2) {
            val a by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.85f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1400
                        delayMillis = i * 180
                        0.3f at 0
                        0.85f at 700
                        0.3f at 1400
                    }
                ),
                label = "dot$i"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(a)
                    .background(color, CircleShape)
            )
        }
    }
}
