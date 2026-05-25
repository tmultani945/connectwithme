package com.sacredflow.app.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.data.local.entity.PrayerEntry
import com.sacredflow.app.domain.usecase.LibraryResurfaceUseCase
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.EmptyState
import com.sacredflow.app.ui.components.PrayerCard
import com.sacredflow.app.ui.components.RuleLine
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.TimeOfDayBackdrop
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onCreate: () -> Unit,
    onPrayForSomeone: () -> Unit,
    onOpenPrayer: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    Box(modifier = Modifier.fillMaxSize().sacredPaper(density = 0.5f)) {
        TimeOfDayBackdrop(height = 300.dp)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Brand mini-bar ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Asterism(size = 8.dp)
                    Text(
                        text = "Connect Yourself",
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.primaryInk
                    )
                }
            }

            // ── Greeting ──
            item {
                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = todayLabel(),
                            style = typo.overline,
                            color = palette.ink3
                        )
                        // Streak appears as a tiny inline pill once the user has
                        // returned at least 2 days. Quiet language, no nagging.
                        if (state.streakDays >= 2) {
                            Text(
                                text = "  ·  ✦ ${state.streakDays} DAYS",
                                style = typo.overline,
                                color = palette.primaryInk
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.greeting,
                        style = MaterialTheme.typography.displayLarge,
                        color = palette.primaryInk
                    )
                }
            }

            // ── Daily reflection hero card ──
            item {
                Spacer(modifier = Modifier.height(20.dp))
                DailyReflectionCard(
                    daily = state.daily,
                    onOpenPrayer = onOpenPrayer,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Demoted "Create another" ──
            // Both secondary CTAs use the same shape: glyph + spacer + label,
            // centered. No inline quota label — the disabled state already
            // communicates "no free reflections left."
            item {
                OutlinedButton(
                    onClick = onCreate,
                    enabled = state.isPlusUser || state.remainingFreeToday > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, palette.outlineSoft)
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Create another",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primaryInk
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // ── Pray for someone you love (Phase 10 entry) ──
            item {
                OutlinedButton(
                    onClick = onPrayForSomeone,
                    enabled = state.isPlusUser || state.remainingFreeToday > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, palette.outlineSoft)
                ) {
                    Text(
                        text = "✦",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Pray for someone you love",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primaryInk
                    )
                }
                Spacer(modifier = Modifier.height(34.dp))
            }

            // ── Resurface from library ──
            if (state.resurface.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader("From your library")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.resurface.forEach { card ->
                            ResurfaceCardView(
                                card = card,
                                onOpen = { onOpenPrayer(card.prayer.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(34.dp))
                }
            }

            // ── Recent section header ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Recent reflections")
                    if (state.recents.isNotEmpty()) {
                        Text(
                            text = "SEE ALL",
                            style = typo.overline,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Recent prayers ──
            if (state.recents.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        EmptyState(
                            title = "Nothing yet.",
                            subtitle = "Your first saved reflection will appear here."
                        )
                    }
                }
            } else {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.recents) { entry ->
                            val accent = when ((entry.id % 3).toInt()) {
                                0 -> palette.primaryInk
                                1 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.tertiary
                            }
                            PrayerCard(
                                recipient = entry.recipient,
                                bodyPreview = entry.bodyText,
                                createdAt = entry.createdAt,
                                isFavorited = entry.id in state.favoriteIds,
                                onClick = { onOpenPrayer(entry.id) },
                                accentColor = accent,
                                wide = false,
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                }
            }

            // ── Quiet quote footer ──
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RuleLine(ornament = true)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "“Begin again, and again, and again.”",
                        style = typo.quoteBody,
                        color = palette.ink3
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyReflectionCard(
    daily: DailyReflectionState,
    onOpenPrayer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val containerColor = palette.primaryInk
    val onContainer = MaterialTheme.colorScheme.background

    Card(
        onClick = {
            if (daily is DailyReflectionState.Ready) onOpenPrayer(daily.prayer.id)
        },
        enabled = daily is DailyReflectionState.Ready,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = onContainer,
            disabledContainerColor = containerColor,
            disabledContentColor = onContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Asterism(size = 6.dp, color = onContainer)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "TODAY'S REFLECTION",
                    style = typo.overline,
                    color = onContainer.copy(alpha = 0.75f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (daily) {
                DailyReflectionState.Loading -> {
                    Text(
                        text = "Preparing something for today…",
                        style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
                        color = onContainer.copy(alpha = 0.85f)
                    )
                }
                is DailyReflectionState.Ready -> {
                    Text(
                        text = "“${daily.prayer.bodyText.firstSentencesFor(maxChars = 220)}”",
                        style = MaterialTheme.typography.titleLarge,
                        color = onContainer,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Open ·  ${daily.prayer.tone.uppercase()}",
                            style = typo.overline,
                            color = onContainer.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = onContainer
                        )
                    }
                }
                is DailyReflectionState.Fallback -> {
                    Text(
                        text = "“${daily.text.firstSentencesFor(220)}”",
                        style = MaterialTheme.typography.titleLarge,
                        color = onContainer,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Offline — saving will resume when you're back online.",
                        style = typo.overline,
                        color = onContainer.copy(alpha = 0.65f)
                    )
                }
                is DailyReflectionState.Failed -> {
                    Text(
                        text = daily.message,
                        style = MaterialTheme.typography.titleMedium,
                        color = onContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResurfaceCardView(
    card: LibraryResurfaceUseCase.ResurfaceCard,
    onOpen: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, palette.outlineSoft)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(
                text = "${card.label.uppercase()}  ·  ${card.prayer.recipient.uppercase()}",
                style = typo.overline,
                color = palette.ink3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "“${card.prayer.bodyText.firstSentencesFor(140)}”",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.ink,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun String.firstSentencesFor(maxChars: Int): String {
    val cleaned = trim()
    if (cleaned.length <= maxChars) return cleaned
    val cut = cleaned.take(maxChars)
    val lastStop = cut.lastIndexOfAny(charArrayOf('.', '!', '?'))
    return if (lastStop > maxChars / 2) cut.substring(0, lastStop + 1) else "$cut…"
}

private fun todayLabel(): String {
    val df = SimpleDateFormat("EEEE · MMM d", Locale.getDefault())
    return df.format(Date()).uppercase()
}
