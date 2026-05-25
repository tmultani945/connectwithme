package com.sacredflow.app.ui.screen.legal

import androidx.compose.runtime.Composable

@Composable
fun TermsScreen(onBack: () -> Unit) {
    LegalScreen(
        title = "Terms of use",
        lastUpdated = "May 24, 2026",
        sections = TermsSections,
        onBack = onBack
    )
}

private val TermsSections: List<Pair<String, String>> = listOf(
    "Acceptance" to
        "By using Connect Yourself you agree to these terms. If you don't agree, please don't use the app.",

    "What Connect Yourself is — and isn't" to
        "Connect Yourself produces written reflections using AI based on inputs you provide. The reflections are generated text, not advice. They are not a substitute for medical, mental-health, religious, legal, or financial guidance. If you are in crisis, please contact a qualified professional or your local emergency services.",

    "Your use" to
        "Use Connect Yourself for your own personal reflection or for prayers you make for people in your life. Don't use it to harass, threaten, or harm another person, to generate sexual content, hate speech, or other content prohibited by our generation provider's policies. We may decline to generate or suspend accounts that violate this.",

    "Generated content" to
        "Reflections are produced by a third-party large-language-model provider. The exact wording will vary, even for identical inputs. We don't guarantee that any reflection is accurate, theologically sound, or therapeutically appropriate. You are responsible for deciding whether and how to act on what you read.",

    "Free use and Plus subscription" to
        "Free users can generate up to five reflections per day. Connect Yourself Plus removes that limit and unlocks long-form prayers, additional voices, and multiple reminders. Plus is sold as a recurring subscription billed by Google Play; you can cancel anytime from your Google Play account.",

    "Intellectual property" to
        "The reflections you generate are yours to keep, share, and adapt. The app itself — its name, design, and code — is the property of its developers. The tone backdrops are licensed photographs.",

    "Disclaimers" to
        "Connect Yourself is provided \"as is\" without warranties of any kind. We don't promise uninterrupted service, freedom from bugs, or fitness for any particular purpose. To the maximum extent permitted by law, we are not liable for indirect or consequential damages arising from your use of the app.",

    "Changes" to
        "We may update these terms. Continued use after a change means you accept the updated terms.",

    "Contact" to
        "Questions about these terms: hello@connectyourself.app"
)
