package com.sacredflow.app.ui.util

/**
 * Single source of truth for the topic suggestion chips. Used by both:
 *  - Onboarding's Topic step (first-run flow)
 *  - Create's Quick Start "About" field + Customize's Topic step
 *
 * Keep them in sync — duplicating this list led to onboarding showing stale
 * options while Create showed fresh ones. Edit once here.
 *
 * Ordering: the first three are the most-shaping life intents (career, love,
 * wealth) so they appear immediately in the horizontally scrolling chip row.
 */
val TopicSuggestions: List<String> = listOf(
    "Achieve Success in Career",
    "Attract love",
    "Bring more wealth",
    "Heal a relationship",
    "Strength in hardship",
    "Better health",
    "Calm my anxiety",
    "A clear decision",
    "Confidence before a meeting",
    "Letting go of fear",
    "Peace before sleep",
    "Forgiveness",
    "Patience with myself",
    "Find my purpose",
    "Gratitude for today",
    "Protect my family"
)
