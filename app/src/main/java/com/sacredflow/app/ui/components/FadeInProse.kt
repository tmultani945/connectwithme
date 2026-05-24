package com.sacredflow.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text

/**
 * Renders a prayer's text with each word fading in sequentially.
 *
 * The effect is achieved by drawing the full text invisibly (so layout never shifts),
 * then overlaying a copy whose alpha animates from 0 → 1 with a stagger per word.
 * To keep it lightweight, we don't animate every word independently — instead we
 * progressively reveal the text by clipping to character index over time.
 *
 * @param text the prayer body. Newlines preserved.
 * @param style the text style to render with.
 * @param color the text color.
 * @param perWordDelayMs how much to delay each word's appearance, in ms.
 * @param initialDelayMs delay before the first word appears.
 */
@Composable
fun FadeInProse(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    perWordDelayMs: Int = 55,
    initialDelayMs: Int = 0
) {
    val totalWords = remember(text) {
        text.split(Regex("\\s+")).count { it.isNotBlank() }
    }
    // Animate a "words visible" counter from 0 to totalWords.
    val visibleWords = remember(text) { Animatable(0f) }
    LaunchedEffect(text) {
        visibleWords.snapTo(0f)
        visibleWords.animateTo(
            targetValue = totalWords.toFloat(),
            animationSpec = tween(
                durationMillis = totalWords * perWordDelayMs,
                delayMillis = initialDelayMs
            )
        )
    }

    val annotated = remember(text, visibleWords.value, color) {
        buildRevealAnnotatedString(text, visibleWords.value, color)
    }

    Text(
        text = annotated,
        style = style,
        modifier = modifier
    )
}

/**
 * Builds an AnnotatedString where words past the current reveal threshold are
 * fully transparent. Words at/before the threshold are fully opaque.
 *
 * This avoids per-word recomposition. The string is rebuilt as a single Text node
 * whenever the reveal counter advances.
 */
private fun buildRevealAnnotatedString(
    text: String,
    wordsVisible: Float,
    color: Color
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var wordIndex = 0
    // Split into tokens, preserving whitespace runs so spacing is unchanged.
    val tokens = text.split(Regex("(\\s+)")).filter { it.isNotEmpty() }
    // Above split loses delimiters; redo with capture group via Regex.findAll.
    val parts = mutableListOf<String>()
    var lastEnd = 0
    for (m in Regex("\\s+").findAll(text)) {
        if (m.range.first > lastEnd) parts.add(text.substring(lastEnd, m.range.first))
        parts.add(m.value)
        lastEnd = m.range.last + 1
    }
    if (lastEnd < text.length) parts.add(text.substring(lastEnd))

    for (part in parts) {
        if (part.isBlank()) {
            builder.append(part)
            continue
        }
        // It's a word. Decide alpha by how close to the reveal frontier we are.
        val distance = wordIndex - wordsVisible + 1f  // 0 = just appeared, 1 = next
        val alpha = (1f - distance.coerceIn(0f, 1f)).coerceIn(0f, 1f)
        builder.withStyle(SpanStyle(color = color.copy(alpha = alpha))) {
            append(part)
        }
        wordIndex++
    }
    return builder.toAnnotatedString()
}
