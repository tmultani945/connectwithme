package com.sacredflow.app.ui.screen.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onCrisisResources: () -> Unit
) {
    val palette = LocalSacredPalette.current
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(title = "Help & disclaimer", onBack = onBack) }
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
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                SectionHeader("About")
                Text(
                    text = "Connect Yourself is a personalization tool for spiritual reflection. " +
                            "You choose how you address what's sacred to you, and we help put your " +
                            "intention into words.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink
                )
                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader("Disclaimer")
                Surface(
                    color = palette.surface2,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Connect Yourself generates text using AI based on your inputs. " +
                                "It is not a substitute for medical, mental health, religious, " +
                                "legal, or financial guidance. If you're in crisis, please reach " +
                                "out to a qualified professional.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.ink,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCrisisResources() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "If you need support",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader("Frequently asked")
                faqItems.forEach { item ->
                    FaqRow(item)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Contact: hello@sacredflow.app",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.ink3
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private data class FaqItem(val question: String, val answer: String)

private val faqItems = listOf(
    FaqItem(
        "Is Connect Yourself tied to any religion?",
        "No. You choose who or what you address — God, Universe, Higher Self, Ancestors, or anything else. The app doesn't mix traditions."
    ),
    FaqItem(
        "Is my data private?",
        "Yes. Your saved prayers live on your device. We don't store the text of your prayers on our servers. Generation requests are anonymous."
    ),
    FaqItem(
        "Why are some lengths locked?",
        "Long-form prayers are a Connect Yourself Plus feature. Short and Medium are always free, up to five generations per day."
    ),
    FaqItem(
        "Where does the text come from?",
        "An AI language model generates the text based on the inputs you choose. The app applies safety guardrails so nothing harmful is created."
    ),
    FaqItem(
        "Can I use this if I'm not religious?",
        "Yes. Reflection, intention, and gratitude don't require a faith tradition. The app meets you where you are."
    ),
    FaqItem(
        "What if a generated prayer doesn't feel right?",
        "Tap Regenerate to try again. Different seeds produce different text. You can also adjust the tone or needs."
    ),
    FaqItem(
        "How do I cancel a subscription?",
        "Open the Play Store, go to Subscriptions, and select Connect Yourself Plus. Cancel anytime."
    ),
    FaqItem(
        "What if I lose my phone?",
        "Saved prayers are stored locally. We're working on optional cloud sync for a future update."
    )
)

@Composable
private fun FaqRow(item: FaqItem) {
    val palette = LocalSacredPalette.current
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.titleMedium,
                color = palette.ink,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = palette.ink3
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.ink2,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
    HorizontalDivider(color = palette.outlineSoft.copy(alpha = 0.4f))
}
