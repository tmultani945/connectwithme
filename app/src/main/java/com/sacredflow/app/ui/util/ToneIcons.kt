package com.sacredflow.app.ui.util

import androidx.annotation.DrawableRes
import com.sacredflow.app.R
import com.sacredflow.app.domain.model.Tone

/**
 * Maps each [Tone] to its hand-drawn brand icon. Kept in the UI layer so the
 * domain model stays free of Android resource references.
 */
@DrawableRes
fun Tone.iconRes(): Int = when (this) {
    Tone.Gentle -> R.drawable.tone_gentle
    Tone.Hopeful -> R.drawable.tone_hopeful
    Tone.Thankful -> R.drawable.tone_thankful
    Tone.Grounded -> R.drawable.tone_grounded
    Tone.Powerful -> R.drawable.tone_powerful
    Tone.Surrendering -> R.drawable.tone_surrendering
}

@DrawableRes
fun Tone.backgroundRes(): Int = when (this) {
    Tone.Gentle -> R.drawable.bg_morning_mist
    Tone.Hopeful -> R.drawable.bg_sunrise
    Tone.Thankful -> R.drawable.bg_candle_light
    Tone.Grounded -> R.drawable.bg_stone_on_sand
    Tone.Powerful -> R.drawable.bg_still_water
    Tone.Surrendering -> R.drawable.bg_horizon_line
}
