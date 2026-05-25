package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import com.sacredflow.app.domain.model.Persona
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.util.iconRes
import com.sacredflow.app.ui.util.rememberSoftTap

/**
 * "What brings you here?" — the persona step.
 *
 * Replaces the original Recipient → Topic → Tone questions for the default
 * onboarding path. Picking a persona pre-fills all three and advances to the
 * Context (name) step. A small text link drops users into the original 4-step
 * customize flow if they want full control.
 */
@Composable
fun OnboardingPersonaScreen(
    navController: NavController,
    onChosen: () -> Unit,
    onCustomize: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val softTap = rememberSoftTap()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "a quiet space") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Asterism(size = 10.dp, color = palette.primaryInk, opacity = 0.85f)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "What brings\nyou here?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Pick whichever feels closest. You can change everything later.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = palette.ink3
                )
                Spacer(modifier = Modifier.height(28.dp))

                Persona.entries.forEach { persona ->
                    PersonaCard(
                        persona = persona,
                        selected = state.persona == persona,
                        onClick = {
                            softTap()
                            viewModel.onAction(OnboardingAction.SetPersona(persona))
                            onChosen()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onCustomize,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Or choose everything yourself  →",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.ink2
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun PersonaCard(
    persona: Persona,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) palette.primaryInk else palette.outlineSoft
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(persona.tone.iconRes()),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                colorFilter = ColorFilter.tint(palette.primaryInk.copy(alpha = 0.75f))
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = persona.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.ink
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = persona.descriptor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = palette.ink3
                )
            }
        }
    }
}
