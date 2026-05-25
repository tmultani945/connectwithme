package com.sacredflow.app.domain.model

/**
 * The six OpenAI TTS voices, surfaced as a user-choosable setting.
 *
 * Two are free (Nova, Onyx) — one warm-feminine, one deep-masculine — so a
 * non-Plus user still has meaningful choice. The remaining four are Plus.
 *
 * [storageKey] matches OpenAI's voice id and is what the worker forwards to
 * `/v1/audio/speech`.
 */
enum class Voice(
    val storageKey: String,
    val displayName: String,
    val descriptor: String,
    val isPlusOnly: Boolean
) {
    Nova("nova", "Nova", "warm, feminine — the default", false),
    Onyx("onyx", "Onyx", "deep, masculine, steady", false),
    Shimmer("shimmer", "Shimmer", "softer feminine, breathy", true),
    Alloy("alloy", "Alloy", "neutral, balanced", true),
    Echo("echo", "Echo", "slightly nasal, masculine", true),
    Fable("fable", "Fable", "British accent, masculine", true);

    companion object {
        fun fromKey(key: String?): Voice =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: Nova
    }
}
