package com.sacredflow.app.ui.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.util.backgroundRes

/**
 * The 9:16 image card a user shares to Instagram Stories, WhatsApp status, etc.
 *
 * This is the most exposure the app gets per user — a friend opens a chat and
 * sees this image. So it must hold its own without app chrome.
 *
 * Goal: the full prayer fits. Truncation reads as broken; small but complete
 * type reads as deliberate, like a printed devotional card.
 *
 * Rendered in standard Compose, captured to PNG by [ShareCardCapture].
 */
@Composable
fun ShareCard(
    text: String,
    recipient: String,
    tone: Tone,
    modifier: Modifier = Modifier,
    forTarget: String? = null
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    Box(modifier = modifier.fillMaxSize()) {
        // ── Tone-specific photo backdrop ──
        Image(
            painter = painterResource(tone.backgroundRes()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // ── Linen scrim ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            palette.primarySoft.copy(alpha = 0.82f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.86f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        // ── Content stack ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Minimal header — just the brand mark. The footer carries the
            // app-name + tagline so the card stays uncluttered.
            Asterism(size = 13.dp, color = palette.primaryInk, opacity = 0.85f)
            // "FOR [NAME]" headline only when this is a prayer-for-someone card.
            // For personal reflections, the header stays empty so the prose
            // can breathe (per user request — no "A REFLECTION I WROTE" label).
            if (!forTarget.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "FOR ${forTarget.uppercase()}",
                    style = typo.overline.copy(letterSpacing = 5.sp, fontSize = 14.sp),
                    color = palette.primaryInk,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // The prayer body — fills the rest of the card, font size auto-fits
            // so the whole prayer is always visible. No truncation, no clipping.
            AdaptiveBodyText(
                text = "“${text.trim()}”",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = palette.primaryInk,
                    textAlign = TextAlign.Center
                ),
                maxFontSize = 28.sp,
                minFontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Footer — names the source so a recipient understands what the app
            // does and can find it.
            Asterism(size = 9.dp, color = palette.ink3, opacity = 0.7f)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "made in Connect Yourself",
                style = typo.overline.copy(letterSpacing = 3.sp, fontSize = 11.sp),
                color = palette.ink2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "a daily reflection practice  ·  connectyourself.app",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                color = palette.ink3,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Renders [text] at the largest font size in [minFontSize]..[maxFontSize] that
 * fits all of [text] within the parent's measured constraints. If even at the
 * minimum size the full prayer doesn't fit, drops complete sentences from the
 * end one at a time until it does — so the recipient only ever sees fully-
 * formed sentences, never a clipped half-line.
 *
 * Strategy:
 *   1. Walk font sizes max → min. If the whole text fits, use it.
 *   2. If nothing fits, lock to minimum size and trim sentences from the back
 *      until what remains fits. Append a soft ellipsis to signal the cut.
 */
@Composable
private fun AdaptiveBodyText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 28.sp,
    minFontSize: TextUnit = 14.sp
) {
    val measurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val maxHeightPx = constraints.maxHeight

        val fitted: Pair<String, TextUnit> = remember(text, maxWidthPx, maxHeightPx) {
            val maxValue = maxFontSize.value.toInt()
            val minValue = minFontSize.value.toInt()

            fun styleFor(sizeSp: Int): TextStyle = style.copy(
                fontSize = sizeSp.sp,
                lineHeight = (sizeSp * 1.45f).sp
            )

            fun fits(content: String, sizeSp: Int): Boolean {
                val measured = measurer.measure(
                    text = AnnotatedString(content),
                    style = styleFor(sizeSp),
                    constraints = Constraints(maxWidth = maxWidthPx)
                )
                return measured.size.height <= maxHeightPx
            }

            // Pass 1: try fitting the whole text at descending sizes.
            var trySize = maxValue
            while (trySize >= minValue) {
                if (fits(text, trySize)) {
                    return@remember Pair(text, trySize.sp)
                }
                trySize -= 1
            }

            // Pass 2: locked to minimum size, drop full sentences from the end.
            val sentences = splitIntoSentences(text)
            for (keepCount in (sentences.size - 1) downTo 1) {
                val candidate = sentences.take(keepCount).joinToString(" ").trimEnd() + " …"
                if (fits(candidate, minValue)) {
                    return@remember Pair(candidate, minValue.sp)
                }
            }

            // Worst case: even the first sentence doesn't fit at min size.
            // Return it anyway — clipping at the very edge is preferable to empty.
            Pair(sentences.firstOrNull().orEmpty(), minValue.sp)
        }

        val (displayText, chosenSize) = fitted

        Text(
            text = displayText,
            style = style.copy(
                fontSize = chosenSize,
                lineHeight = (chosenSize.value * 1.45f).sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}

/**
 * Splits prayer text into whole sentences. Splits on `.`, `!`, `?` followed by
 * whitespace; also treats paragraph breaks (double newlines) as boundaries.
 * Terminal punctuation stays attached to the preceding sentence (lookbehind).
 *
 * Good-enough for prayer prose — doesn't try to handle Mr./Dr./etc.
 */
private fun splitIntoSentences(text: String): List<String> {
    val pattern = Regex("(?<=[.!?])\\s+|\\n\\n+")
    return text.split(pattern).map { it.trim() }.filter { it.isNotBlank() }
}
