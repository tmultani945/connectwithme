package com.sacredflow.app.ui.screen.legal

import androidx.compose.runtime.Composable

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    LegalScreen(
        title = "Privacy",
        lastUpdated = "May 24, 2026",
        sections = PrivacyPolicySections,
        onBack = onBack
    )
}

private val PrivacyPolicySections: List<Pair<String, String>> = listOf(
    "What this is" to
        "Connect Yourself helps you write personal reflections in your own voice. This policy explains what data we collect, why, and how you can control it.",

    "What stays on your device" to
        "Your saved reflections, mood entries, streak counters, voice and tone preferences, custom recipient names, and the optional name you give yourself all live in a local database on your phone. We do not copy them to our servers.",

    "What we send when you generate a reflection" to
        "When you tap Generate, we send your choices (recipient, tone, topic, optional context, your name if given) to our generation service. The service forwards them to a third-party large-language-model provider (currently Anthropic), which returns the reflection text. The same applies to voice playback (currently OpenAI text-to-speech). We do not store the request or response text on our servers beyond what's needed to complete the request and produce billing metrics.",

    "Anonymous install identifier" to
        "We generate a random install identifier (a UUID) on first launch and include it with generation requests for rate-limiting and abuse prevention. It is not tied to any account, email, or device identifier and cannot be used to contact you.",

    "Crisis safety" to
        "If your input contains language suggesting self-harm or imminent danger, the app routes you to crisis resources instead of generating a reflection. This check runs partly on-device and partly via the language model's own safety filters.",

    "Subscriptions and billing" to
        "Subscriptions are processed by Google Play. We never see your payment information. We do receive an opaque purchase token to verify whether your subscription is active.",

    "Sharing reflections" to
        "When you share a reflection card from the app, the image is created locally and handed to the system share sheet. The destination (Messages, Instagram, etc.) is your choice; we don't see what you send or to whom.",

    "Data you control" to
        "You can delete a saved reflection from its detail screen. You can change or clear your name, voice, and tone defaults at any time in Settings. To erase everything the app has stored, uninstall the app — Android will delete its local storage. To stop subscription auto-renewal, manage it from your Google Play account.",

    "Children" to
        "Connect Yourself is not directed at children under 13. If you believe a child has installed it, please uninstall and contact us.",

    "Changes" to
        "We will update this page if our practices change and post the new date at the top.",

    "Contact" to
        "Questions or requests about your data: hello@connectyourself.app"
)
