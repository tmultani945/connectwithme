package com.sacredflow.app.ui.screen.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.domain.model.Length
import com.sacredflow.app.domain.model.Recipient
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.components.DenseChip
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.backgroundRes
import com.sacredflow.app.ui.util.iconRes
import kotlinx.coroutines.delay

private enum class CreateStep {
    QUICK_START, RECIPIENT, TOPIC, TONE, DETAILS;

    /** True for the four customization steps that share the progress-dot top bar. */
    val isFormStep: Boolean get() = this != QUICK_START

    /** 0-based index within the form path (0..3). Used for the progress-dot display. */
    val formStepOrdinal: Int get() = ordinal - 1

    companion object {
        /** Number of dots in the form-path top bar. Does not include QUICK_START. */
        const val FORM_COUNT = 4
        fun fromOrdinal(o: Int): CreateStep = entries.firstOrNull { it.ordinal == o } ?: QUICK_START
    }
}

// Single source of truth lives in ui/util/TopicSuggestions.kt. Aliased here so
// the existing call sites can keep their TOPIC_SUGGESTIONS reference.
private val TOPIC_SUGGESTIONS = com.sacredflow.app.ui.util.TopicSuggestions

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
    // Enter Create on the Quick Start summary by default. The form path is a
    // side branch reached via the Customize link.
    var step by remember { mutableStateOf(CreateStep.QUICK_START) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CreateEvent.NavigateToLoading -> onGenerate()
                CreateEvent.ShowPaywall -> onPaywall()
                is CreateEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val onBackInternal: () -> Unit = {
        when (step) {
            CreateStep.QUICK_START -> onBack()
            // Backing out of the first form step returns to the Quick Start summary
            // rather than exiting — so Customize feels like a true side branch.
            CreateStep.RECIPIENT -> { step = CreateStep.QUICK_START }
            else -> { step = CreateStep.fromOrdinal(step.ordinal - 1) }
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
                CreateFlowTopBar(step = step, onBack = onBackInternal)

                AnimatedContent(
                    targetState = step,
                    label = "createStep",
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        (slideInHorizontally(
                            animationSpec = tween(380),
                            initialOffsetX = if (forward) IntoFromRight else IntoFromLeft
                        ) + fadeIn(animationSpec = tween(380)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(300),
                                    targetOffsetX = if (forward) OutToLeft else OutToRight
                                ) + fadeOut(animationSpec = tween(250))
                            )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { current ->
                    when (current) {
                        CreateStep.QUICK_START -> StepQuickStart(
                            state = state,
                            onAction = viewModel::onAction,
                            onCustomize = { step = CreateStep.RECIPIENT },
                            onEditRecipient = { step = CreateStep.RECIPIENT },
                            onEditTone = { step = CreateStep.TONE },
                            onSubmit = { viewModel.onAction(CreateAction.Submit) }
                        )
                        CreateStep.RECIPIENT -> StepRecipient(
                            state = state,
                            onAction = viewModel::onAction,
                            onAdvance = { step = CreateStep.TOPIC }
                        )
                        CreateStep.TOPIC -> StepTopic(
                            state = state,
                            onAction = viewModel::onAction,
                            onAdvance = { step = CreateStep.TONE }
                        )
                        CreateStep.TONE -> StepTone(
                            state = state,
                            onAction = viewModel::onAction,
                            onAdvance = { step = CreateStep.DETAILS }
                        )
                        CreateStep.DETAILS -> StepDetails(
                            state = state,
                            onAction = viewModel::onAction,
                            onSubmit = { viewModel.onAction(CreateAction.Submit) }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CreateFlowTopBar(step: CreateStep, onBack: () -> Unit) {
    val palette = LocalSacredPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = palette.ink2
            )
        }
        // Hide the progress dots on the Quick Start summary — it's not part of
        // the customize ritual, it's the entry. Dots only mean something for the
        // four form steps.
        if (step.isFormStep) {
            StepDots(current = step.formStepOrdinal, total = CreateStep.FORM_COUNT)
        } else {
            Spacer(modifier = Modifier.size(0.dp))
        }
        Spacer(modifier = Modifier.size(44.dp))
    }
}

@Composable
private fun StepDots(current: Int, total: Int) {
    val palette = LocalSacredPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until total) {
            val color = when {
                i < current -> palette.primaryInk.copy(alpha = 0.35f)
                i == current -> palette.primaryInk
                else -> palette.outlineSoft
            }
            val size = if (i == current) 7.dp else 5.dp
            Box(modifier = Modifier.size(size).background(color, CircleShape))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step shell — question, helper, body, optional bottom action
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepShell(
    question: String,
    helper: String?,
    bottomAction: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit
) {
    val palette = LocalSacredPalette.current

    var titleVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(180)
        titleVisible = true
    }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(28.dp))
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(400)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Column {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.primaryInk
                    )
                    if (!helper.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = helper,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            color = palette.ink3
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) { body() }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (bottomAction != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                bottomAction()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Start — pre-filled summary; two taps from Home to a generated reflection.
// The 4-step form lives behind a "Customize" link as a side branch.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepQuickStart(
    state: CreateState,
    onAction: (CreateAction) -> Unit,
    onCustomize: () -> Unit,
    onEditRecipient: () -> Unit,
    onEditTone: () -> Unit,
    onSubmit: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    StepShell(
        question = "A reflection,\nwith your usual setup.",
        helper = "or change something below.",
        bottomAction = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = if (state.isSubmitting) "Listening…" else "Generate",
                    onClick = onSubmit,
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onCustomize,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Customize  →",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.ink2
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            QuickStartLine(
                label = "TO",
                value = state.recipient.displayName,
                onTap = onEditRecipient
            )
            QuickStartLine(
                label = "TONE",
                value = state.tone.displayName,
                onTap = onEditTone
            )
            // Mood — optional. Folded into the prompt as a felt-state hint and
            // logged for the mood history (Settings, future iteration).
            Column {
                Text("FEELING (OPTIONAL)", style = typo.overline, color = palette.ink3)
                Spacer(modifier = Modifier.height(8.dp))
                com.sacredflow.app.ui.components.MoodPicker(
                    selected = state.mood,
                    onSelect = { onAction(CreateAction.SetMood(it)) }
                )
            }
            Column {
                Text("ABOUT (OPTIONAL)", style = typo.overline, color = palette.ink3)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(TOPIC_SUGGESTIONS) { suggestion ->
                        SuggestionChip(
                            label = suggestion,
                            selected = state.topic == suggestion,
                            onClick = { onAction(CreateAction.SetTopic(suggestion)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.topic,
                    onValueChange = { onAction(CreateAction.SetTopic(it)) },
                    placeholder = {
                        Text(
                            "What's on your mind today?",
                            color = palette.ink3,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                        )
                    },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Clearance for the overlay bottomAction (PrimaryButton + Customize link)
            // so the last form field isn't hidden behind it.
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

@Composable
private fun QuickStartLine(
    label: String,
    value: String,
    onTap: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    Column(modifier = Modifier.fillMaxWidth().clickable { onTap() }) {
        Text(label, style = typo.overline, color = palette.ink3)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = palette.primaryInk
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 1 — Recipient
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepRecipient(
    state: CreateState,
    onAction: (CreateAction) -> Unit,
    onAdvance: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val canContinue = if (state.isCustomRecipientMode) {
        state.customRecipientDraft.isNotBlank()
    } else {
        // A built-in recipient is selected (recipient defaults to "Universe" so always valid).
        !state.recipient.isCustom
    }
    StepShell(
        question = "To whom\nshall this be addressed?",
        helper = "or to what",
        bottomAction = {
            PrimaryButton(
                text = "Continue",
                onClick = onAdvance,
                enabled = canContinue
            )
        }
    ) {
        Column {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (Recipient.BuiltInOptions + state.customRecipientChoices.map { Recipient.Custom(it) })
                    .distinct()
                    .forEach { option ->
                        val isSelected = state.recipient.displayName == option.displayName &&
                                state.recipient.isCustom == option.isCustom &&
                                !state.isCustomRecipientMode
                        DenseChip(
                            label = option.displayName,
                            selected = isSelected,
                            // Selecting a built-in only sets state — no auto-advance.
                            onClick = { onAction(CreateAction.SetRecipient(option)) }
                        )
                    }
                DenseChip(
                    label = "Custom…",
                    selected = state.isCustomRecipientMode,
                    leadingIcon = Icons.Outlined.Add,
                    onClick = { onAction(CreateAction.EnableCustomRecipientMode(true)) }
                )
            }
            AnimatedVisibility(visible = state.isCustomRecipientMode) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.customRecipientDraft,
                        onValueChange = { onAction(CreateAction.SetCustomRecipientDraft(it)) },
                        placeholder = { Text("e.g., Source, Spirit, Mother Earth", color = palette.ink3) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 2 — Topic (textbox + suggestion chips)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepTopic(
    state: CreateState,
    onAction: (CreateAction) -> Unit,
    onAdvance: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val suggestionsState = rememberLazyListState()

    StepShell(
        question = "What is\nthis about?",
        helper = "the topic, in your words",
        bottomAction = {
            PrimaryButton(
                text = "Continue",
                onClick = onAdvance,
                enabled = state.topic.isNotBlank()
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Suggestion chips — horizontal scroll above the textbox
            Text(
                text = "TRY ONE",
                style = typo.overline,
                color = palette.ink3
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                state = suggestionsState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(TOPIC_SUGGESTIONS) { suggestion ->
                    SuggestionChip(
                        label = suggestion,
                        selected = state.topic == suggestion,
                        onClick = { onAction(CreateAction.SetTopic(suggestion)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Freeform textbox
            OutlinedTextField(
                value = state.topic,
                onValueChange = { onAction(CreateAction.SetTopic(it)) },
                placeholder = {
                    Text(
                        "Write in your own words…",
                        color = palette.ink3,
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                    )
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Be as specific or open as you like.",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.ink3
                )
                Text(
                    text = "${state.topic.length} / ${CreateState.MAX_TOPIC_LENGTH}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.ink3
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalSacredPalette.current
    val softTap = com.sacredflow.app.ui.util.rememberSoftTap()
    Box(
        modifier = Modifier
            .height(36.dp)
            .clickable { softTap(); onClick() }
            .background(
                if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface,
                PillShape
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) palette.primaryInk else palette.ink2
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 3 — Tone (horizontal swipe cards, auto-advance on tap)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepTone(
    state: CreateState,
    onAction: (CreateAction) -> Unit,
    onAdvance: () -> Unit
) {
    val listState = rememberLazyListState()
    StepShell(
        question = "How should\nyou address?",
        helper = "the tone of voice",
        bottomAction = {
            PrimaryButton(text = "Continue", onClick = onAdvance)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(end = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(Tone.entries.toList()) { tone ->
                    InlineToneCard(
                        tone = tone,
                        selected = state.tone == tone,
                        // Selecting only sets state — user advances via Continue.
                        onClick = { onAction(CreateAction.SetTone(tone)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineToneCard(tone: Tone, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val shape = RoundedCornerShape(20.dp)
    val softTap = com.sacredflow.app.ui.util.rememberSoftTap()
    Card(
        modifier = Modifier.width(240.dp).height(280.dp).clickable { softTap(); onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) palette.primaryInk else palette.outlineSoft
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().clip(shape)) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(tone.backgroundRes()),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Linen scrim — denser at the bottom so prose stays readable.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                    .copy(alpha = 0.40f),
                                (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                    .copy(alpha = 0.78f),
                                (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                    .copy(alpha = 0.92f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selected) "SELECTED" else "TONE",
                        style = typo.overline,
                        color = if (selected) palette.primaryInk else palette.ink3
                    )
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(tone.iconRes()),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            (if (selected) palette.primaryInk else palette.ink2).copy(alpha = 0.7f)
                        )
                    )
                }
                Text(text = tone.displayName, style = MaterialTheme.typography.displaySmall, color = palette.primaryInk)
                Text(text = tone.descriptor, style = MaterialTheme.typography.bodySmall, color = palette.ink2)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "“${tone.sampleLine}”",
                    style = typo.quoteBody.copy(fontSize = 17.sp),
                    color = palette.primaryInk.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 4 — Name + anything specific + length + Generate
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepDetails(
    state: CreateState,
    onAction: (CreateAction) -> Unit,
    onSubmit: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    StepShell(question = "A few last\nthings", helper = "all optional") {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Text("YOUR NAME", style = typo.overline, color = palette.ink3)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.userName,
                onValueChange = { onAction(CreateAction.SetUserName(it)) },
                placeholder = { Text("So the prayer can address you", color = palette.ink3) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("ANYTHING SPECIFIC", style = typo.overline, color = palette.ink3)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.userContext,
                onValueChange = { onAction(CreateAction.SetUserContext(it)) },
                placeholder = { Text("e.g., this is for my grandmother", color = palette.ink3) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Stays on your device.", style = MaterialTheme.typography.bodySmall, color = palette.ink3)
                Text(
                    "${state.userContext.length} / ${CreateState.MAX_CONTEXT_LENGTH}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.ink3
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("LENGTH", style = typo.overline, color = palette.ink3)
            Spacer(modifier = Modifier.height(8.dp))
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
                            .height(48.dp)
                            .clickable(enabled = !locked) { onAction(CreateAction.SetLength(length)) }
                            .background(
                                if (on) MaterialTheme.colorScheme.surface else Color.Transparent,
                                PillShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
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
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = if (state.showOutOfQuota) "Out of free — Sacred Flow Plus" else "Generate",
                onClick = onSubmit,
                enabled = state.isValid && !state.isSubmitting,
                isLoading = state.isSubmitting
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = {
                    onAction(CreateAction.SetUserName(""))
                    onAction(CreateAction.SetUserContext(""))
                    onSubmit()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip — just generate", color = palette.ink3)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom-right circular Next button
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NextButton(onClick: () -> Unit) {
    val palette = LocalSacredPalette.current
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = palette.primaryInk,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = "Continue",
            modifier = Modifier.size(26.dp)
        )
    }
}

private val IntoFromRight: (Int) -> Int = { it / 3 }
private val IntoFromLeft: (Int) -> Int = { -it / 3 }
private val OutToLeft: (Int) -> Int = { -it / 3 }
private val OutToRight: (Int) -> Int = { it / 3 }
