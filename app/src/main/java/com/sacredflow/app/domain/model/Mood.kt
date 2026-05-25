package com.sacredflow.app.domain.model

/**
 * Six word-based mood states the user can mark before generating a reflection.
 *
 * Deliberately not emoji — the app's visual language is restrained Fraunces +
 * letterpress. The labels run roughly from "carrying something heavy" up to
 * "lit up and present," without forcing a strict valence on the user.
 *
 * [descriptor] is the phrase folded into the generation prompt as user-context.
 */
enum class Mood(
    val storageKey: String,
    val displayName: String,
    val descriptor: String
) {
    Heavy(
        storageKey = "heavy",
        displayName = "Heavy",
        descriptor = "I'm carrying something heavy right now."
    ),
    Tired(
        storageKey = "tired",
        displayName = "Tired",
        descriptor = "I'm tired and low on energy."
    ),
    Steady(
        storageKey = "steady",
        displayName = "Steady",
        descriptor = "I'm steady — neither up nor down."
    ),
    Peaceful(
        storageKey = "peaceful",
        displayName = "Peaceful",
        descriptor = "I'm feeling calm and settled."
    ),
    Becoming(
        storageKey = "becoming",
        displayName = "Becoming",
        descriptor = "Something is shifting in me. I'm leaning into it."
    ),
    Awake(
        storageKey = "awake",
        displayName = "Awake",
        descriptor = "I'm awake, alert, ready to meet the day."
    );

    companion object {
        fun fromKey(key: String?): Mood? =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) }
    }
}
