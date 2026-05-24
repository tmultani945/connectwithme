package com.sacredflow.app.ui.screen.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Need
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.domain.model.UseCase
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.ChipCloud
import com.sacredflow.app.ui.components.ChipItem
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(
    onGenerate: () -> Unit,
    onPaywall: () -> Unit,
    onBack: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CreateEvent.NavigateToLoading -> onGenerate()
                CreateEvent.ShowPaywall -> onPaywall()
                is CreateEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            BackTopBar(
                onBack = onBack,
                title = "New reflection",
                sub = "step into what's here"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── Type ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Type")
                    SoftChipRow {
                        UseCase.entries.forEach { uc ->
                            DenseChip(
                                label = uc.displayName,
                                selected = state.useCase == uc,
                                onClick = { viewModel.onAction(CreateAction.SetUseCase(uc)) }
                            )
                        }
                    }
                }

                // ── Recipient ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Addressed to")
                    SoftChipRow {
                        (Recipient.BuiltInOptions + state.customRecipientChoices.map { Recipient.Custom(it) })
                            .distinct()
                            .forEach { option ->
                                val isSelected = state.recipient.displayName == option.displayName &&
                                        state.recipient.isCustom == option.isCustom
                                DenseChip(
                                    label = option.displayName,
                                    selected = isSelected,
                                    onClick = { viewModel.onAction(CreateAction.SetRecipient(option)) }
                                )
                            }
                        DenseChip(
                            label = "New custom…",
                            selected = state.isCustomRecipientMode &&
                                    state.recipient.displayName == state.customRecipientDraft,
                            leadingIcon = Icons.Outlined.Add,
                            onClick = { viewModel.onAction(CreateAction.EnableCustomRecipientMode(true)) }
                        )
                    }
                    AnimatedVisibility(visible = state.isCustomRecipientMode) {
                        OutlinedTextField(
                            value = state.customRecipientDraft,
                            onValueChange = { viewModel.onAction(CreateAction.SetCustomRecipientDraft(it)) },
                            placeholder = { Text("e.g., Source, Spirit, Mother Earth") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                    if (state.customRecipientChoices.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.surface2, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Bookmark,
                                contentDescription = null,
                                tint = palette.ink3,
                                modifier = Modifier.size(13.dp)
                            )
                            Text("RECENT:", style = typo.overline, color = palette.ink3)
                            Text(
                                text = state.customRecipientChoices.joinToString(" · "),
                                style = typo.quoteBody.copy(fontStyle = FontStyle.Italic),
                                color = palette.ink2
                            )
                        }
                    }
                }

                // ── Needs ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SectionHeader("What you'd like to bring in")
                        Text(
                            text = "${state.needs.size} OF ${Need.MAX_SELECTABLE}",
                            style = typo.overline,
                            color = palette.ink3
                        )
                    }
                    ChipCloud(
                        items = Need.entries.map { need ->
                            ChipItem(
                                value = need,
                                label = need.displayName,
                                enabled = need in state.needs || state.needs.size < Need.MAX_SELECTABLE
                            )
                        },
                        selected = state.needs,
                        onToggle = { viewModel.onAction(CreateAction.ToggleNeed(it)) }
                    )
                }

                // ── Tone ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Tone")
                    SoftChipRow {
                        Tone.entries.forEach { tone ->
                            DenseChip(
                                label = tone.displayName,
                                selected = state.tone == tone,
                                onClick = { viewModel.onAction(CreateAction.SetTone(tone)) }
                            )
                        }
                    }
                }

                // ── Length (custom pill segmented) ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Length")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.surface2, PillShape)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Length.entries.forEach { length ->
                            val on = state.length == length
                            val locked = length.isPlusOnly && !state.canSelectLong
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clickable(enabled = !locked) {
                                        viewModel.onAction(CreateAction.SetLength(length))
                                    }
                                    .background(
                                        if (on) MaterialTheme.colorScheme.surface else Color.Transparent,
                                        PillShape
                                    )
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = length.displayName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (on) palette.primaryInk else palette.ink2
                                        )
                                        if (locked) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Outlined.Lock,
                                                contentDescription = "Plus only",
                                                tint = palette.ink3,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = length.approximateWords,
                                        style = typo.overline.copy(letterSpacing = androidx.compose.ui.unit.TextUnit(0.2f, androidx.compose.ui.unit.TextUnitType.Sp)),
                                        color = palette.ink3
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Context ──
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Personal context · optional")
                    OutlinedTextField(
                        value = state.userContext,
                        onValueChange = { viewModel.onAction(CreateAction.SetUserContext(it)) },
                        placeholder = { Text("A few words about what's on your mind…") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stays on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.ink3
                        )
                        Text(
                            text = "${state.userContext.length} / ${CreateState.MAX_CONTEXT_LENGTH}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.ink3
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    TextButton(onClick = { viewModel.onAction(CreateAction.ResetToDefaults) }) {
                        Text("↺  Reset to defaults", color = palette.ink3)
                    }
                }
            }

            // Pinned bottom Generate
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                PrimaryButton(
                    text = if (state.showOutOfQuota) "Out of free — Connect Yourself Plus" else "Generate",
                    onClick = { viewModel.onAction(CreateAction.Submit) },
                    enabled = state.isValid && !state.isSubmitting,
                    isLoading = state.isSubmitting
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SoftChipRow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun DenseChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val palette = LocalSacredPalette.current
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = PillShape,
        leadingIcon = when {
            selected -> {
                { Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
            }
            leadingIcon != null -> {
                { Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(14.dp)) }
            }
            else -> null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = palette.ink,
            iconColor = palette.ink2,
            selectedContainerColor = palette.primaryInk,
            selectedLabelColor = MaterialTheme.colorScheme.background,
            selectedLeadingIconColor = MaterialTheme.colorScheme.background
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = palette.outlineSoft,
            selectedBorderColor = palette.primaryInk
        )
    )
}
