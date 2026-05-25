package com.sacredflow.app.ui.screen.prayfor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.DenseChip
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

@Composable
fun PrayForScreen(
    onGenerate: () -> Unit,
    onPaywall: () -> Unit,
    onBack: () -> Unit,
    viewModel: PrayForViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                PrayForEvent.NavigateToLoading -> onGenerate()
                PrayForEvent.ShowPaywall -> onPaywall()
                is PrayForEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Top bar — just a back button. No step dots: this is a single screen. ──
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(8.dp).size(44.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.ink2
                    )
                }

                // ── Body ──
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
                        text = "Hold someone\nin light.",
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "A short prayer, written in your voice, for someone you love.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                        color = palette.ink3
                    )
                    Spacer(modifier = Modifier.height(36.dp))

                    // Name input — the only required field.
                    Text("WHO ARE YOU HOLDING?", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.targetName,
                        onValueChange = { viewModel.onAction(PrayForAction.SetTargetName(it)) },
                        placeholder = {
                            Text(
                                "Mom · Sarah · my child · a friend",
                                color = palette.ink3,
                                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Optional situation — adds depth without making it required.
                    Text("WHAT'S HAPPENING (OPTIONAL)", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.situation,
                        onValueChange = { viewModel.onAction(PrayForAction.SetSituation(it)) },
                        placeholder = {
                            Text(
                                "Before her surgery · grieving · starting something new",
                                color = palette.ink3,
                                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                            )
                        },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Tone picker — horizontal scroll of dense chips. Default Gentle
                    // suits most intercessions but the full set is available.
                    Text("TONE", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(Tone.entries.toList()) { tone ->
                            DenseChip(
                                label = tone.displayName,
                                selected = state.tone == tone,
                                onClick = { viewModel.onAction(PrayForAction.SetTone(tone)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    PrimaryButton(
                        text = if (state.isSubmitting) "Listening…" else "Generate",
                        onClick = { viewModel.onAction(PrayForAction.Submit) },
                        enabled = state.isValid && !state.isSubmitting
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
