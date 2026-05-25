package com.sacredflow.app.ui.screen.generation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.FadeInProse
import com.sacredflow.app.ui.components.IconActionPill
import com.sacredflow.app.ui.components.OfflineBanner
import com.sacredflow.app.ui.components.RecipientBadge
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.components.sacredVignette
import com.sacredflow.app.ui.practice.PracticeSheet
import com.sacredflow.app.ui.practice.rememberPracticeState
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.backgroundRes

@Composable
fun ResultScreen(
    onDone: () -> Unit,
    onOpenSaved: (Long) -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val practice = rememberPracticeState(text = state.text)
    val palette = LocalSacredPalette.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ResultEvent.CopiedToClipboard -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Connect Yourself", event.text))
                    snackbarHostState.showSnackbar("Copied")
                }
                is ResultEvent.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, event.text)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
                ResultEvent.NavigateHome -> onDone()
                is ResultEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(state.noActiveResult) {
        if (state.noActiveResult) onDone()
    }

    // Tone-adaptive backdrop: each tone has a paired Unsplash photo (morning mist,
    // sunrise, candle light, stone on sand, still water, horizon line). The photo
    // sits behind a denser linen scrim so the prose stays the focus — the image
    // reads as a felt atmosphere, not a busy background.
    val tone = state.request?.tone?.let { Tone.fromKey(it) } ?: Tone.Gentle

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(tone.backgroundRes()),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Linen scrim — denser than the original gradient so the photo can
            // sit underneath without fighting the prose for attention. Warm at top
            // so the recipient badge feels lit, opaque toward the bottom so the
            // action pills sit cleanly.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF6EBD6).copy(alpha = 0.78f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.92f)
                            )
                        )
                    )
                    .sacredPaper()
                    .sacredVignette(strength = 0.08f)
            )
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Top bar — close · recipient · menu (menu is decorative for now) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = viewModel::onDone) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = palette.ink2
                        )
                    }
                    state.request?.let { req ->
                        RecipientBadge(text = "to ${req.recipient}")
                    }
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // ── Scrolling body ──
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp)
                ) {
                    AnimatedVisibility(visible = state.isFallback) {
                        Column {
                            OfflineBanner()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Asterism(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Drop cap + prayer body with word-by-word reveal.
                    PrayerBodyWithDropCap(
                        text = state.text,
                        animate = !state.isFallback && state.text.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(36.dp))
                    Asterism(modifier = Modifier.align(Alignment.CenterHorizontally), size = 9.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    state.request?.let { req ->
                        Text(
                            text = "Tone · ${req.tone}   ·   Need · ${req.needs.joinToString(", ")}",
                            style = LocalSacredTypography.current.overline,
                            color = palette.ink3,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Length · ${req.length}",
                            style = LocalSacredTypography.current.overline,
                            color = palette.ink3,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(96.dp))
                }

                // ── Action row + Done button (pinned bottom) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconActionPill(
                        icon = if (state.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        label = if (state.isSaved) "Saved" else "Save",
                        onClick = {
                            if (state.isSaved) state.savedPrayerId?.let { onOpenSaved(it) }
                            else viewModel.onSave()
                        },
                        enabled = !state.isFallback || state.isSaved,
                        tint = if (state.isSaved) MaterialTheme.colorScheme.tertiary else palette.primaryInk
                    )
                    IconActionPill(
                        icon = Icons.Outlined.RecordVoiceOver,
                        label = "Speak after me",
                        onClick = practice::start,
                        enabled = state.text.isNotBlank()
                    )
                    IconActionPill(
                        icon = Icons.Outlined.Refresh,
                        label = "Again",
                        onClick = viewModel::onRegenerate,
                        enabled = !state.isRegenerating
                    )
                    IconActionPill(
                        icon = Icons.Outlined.IosShare,
                        label = "Share",
                        onClick = viewModel::onShare
                    )
                    IconActionPill(
                        icon = Icons.Outlined.ContentCopy,
                        label = "Copy",
                        onClick = viewModel::onCopy
                    )
                }

                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    OutlinedButton(
                        onClick = viewModel::onDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.primaryInk,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Done", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            PracticeSheet(state = practice)
        }
    }
}

/**
 * Renders the first letter of the prayer as a large italic drop cap, with the
 * remainder of the text flowing beside it. The body uses [FadeInProse] for the
 * word-by-word reveal effect on first display.
 */
@Composable
private fun PrayerBodyWithDropCap(
    text: String,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    if (text.isBlank()) return
    val first = text.first()
    val rest = text.substring(1)
    Box(modifier = modifier.fillMaxWidth()) {
        // Drop cap, positioned to the upper-left, outside the text column.
        Text(
            text = first.toString(),
            style = typo.dropCap,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .offset(x = (-4).dp, y = (-10).dp)
        )
        // Prayer body with left padding equal to drop-cap width.
        SelectionContainer(
            modifier = Modifier.padding(start = 52.dp)
        ) {
            if (animate) {
                FadeInProse(
                    text = rest,
                    style = typo.prayerText,
                    color = palette.primaryInk,
                    perWordDelayMs = 55
                )
            } else {
                Text(
                    text = rest,
                    style = typo.prayerText,
                    color = palette.primaryInk
                )
            }
        }
    }
}
