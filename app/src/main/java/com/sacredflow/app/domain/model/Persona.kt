package com.sacredflow.app.domain.model

/**
 * "What brings you here?" — four archetypes the user picks during onboarding.
 *
 * Each persona seeds the first reflection: it pre-selects a [Recipient], [Tone],
 * and topic phrase that match the emotional state the user reported. The user
 * still gets to add their name in the next step; everything else is filled in.
 *
 * If the user wants full control instead, they can pick the explicit "choose
 * everything myself" exit from the Persona screen and walk the original
 * recipient → topic → tone → context flow.
 */
enum class Persona(
    val storageKey: String,
    val displayName: String,
    val descriptor: String,
    val recipientDisplayName: String,
    val tone: Tone,
    val topicSeed: String
) {
    Anxious(
        storageKey = "anxious",
        displayName = "I'm anxious",
        descriptor = "I want to feel steady.",
        recipientDisplayName = "Universe",
        tone = Tone.Grounded,
        topicSeed = "the worry I'm carrying right now"
    ),
    HardMoment(
        storageKey = "hard_moment",
        displayName = "I'm in a hard moment",
        descriptor = "I want to be held, not fixed.",
        recipientDisplayName = "Universe",
        tone = Tone.Gentle,
        topicSeed = "what I'm carrying tonight"
    ),
    Manifesting(
        storageKey = "manifesting",
        displayName = "I want to manifest something",
        descriptor = "I want to call it toward me.",
        recipientDisplayName = "Universe",
        tone = Tone.Powerful,
        topicSeed = "what I'm calling toward me"
    ),
    QuietMoment(
        storageKey = "quiet_moment",
        displayName = "I just need a quiet moment",
        descriptor = "I want to lay something down.",
        recipientDisplayName = "Higher Self",
        tone = Tone.Surrendering,
        topicSeed = "the noise I'm releasing"
    );

    companion object {
        fun fromKey(key: String?): Persona? =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) }
    }
}
