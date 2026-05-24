package com.sacredflow.app.ui.screen.home

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    Text(
                        text = todayLabel(),
                        style = typo.overline,
                        color = palette.ink3
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.greeting,
                        style = MaterialTheme.typography.displayLarge,
                        color = palette.primaryInk
                    )
                    Text(
                        text = "What would you like to bring into today?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.ink2
                    )
                }
            }

            // ── Primary CTA card ──
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    onClick = onCreate,
                    enabled = state.isPlusUser || state.remainingFreeToday > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = palette.primaryInk,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create a reflection",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.background
                            )
                            Text(
                                text = state.quotaLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
                Spacer(modifier = Modifier.height(34.dp))
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

private fun todayLabel(): String {
    val df = SimpleDateFormat("EEEE · MMM d", Locale.getDefault())
    return df.format(Date()).uppercase()
}
