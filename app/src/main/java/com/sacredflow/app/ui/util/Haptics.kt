package com.sacredflow.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Small haptic helpers so the interaction layer matches the visual layer.
 * The eye is well-fed in this app (Fraunces, tone photos, paper grain) — the
 * hand should feel something too.
 *
 *  - [softTap] : a quiet tick — use for selection (tone, recipient, chip).
 *  - [firmTap] : a heavier thump — use for commit actions (Generate, Save).
 */
@Composable
fun rememberSoftTap(): () -> Unit {
    val haptic: HapticFeedback = LocalHapticFeedback.current
    return remember(haptic) { { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) } }
}

@Composable
fun rememberFirmTap(): () -> Unit {
    val haptic: HapticFeedback = LocalHapticFeedback.current
    return remember(haptic) { { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } }
}
