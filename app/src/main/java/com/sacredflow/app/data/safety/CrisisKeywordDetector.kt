package com.sacredflow.app.data.safety

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Word-boundary regex screen for the most common phrasings.
 * Intentionally narrow — false negatives are handled server-side.
 * False positives must be avoided because they will block legitimate
 * users from generating prayers about grief, illness, and recovery.
 */
@Singleton
class CrisisKeywordDetector @Inject constructor() {

    private val selfHarmPatterns: List<Regex> = listOf(
        // explicit intent
        Regex("""\b(want|wanted|wanting|going)\s+to\s+(kill|end)\s+myself\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(i|i'?m|i\s+am)\s+going\s+to\s+(kill|end)\s+myself\b""", RegexOption.IGNORE_CASE),
        Regex("""\bend\s+(my|it\s+all)\b""", RegexOption.IGNORE_CASE),
        Regex("""\bdon'?t\s+want\s+to\s+(live|be\s+here|exist)\s+anymore\b""", RegexOption.IGNORE_CASE),
        Regex("""\bsuicid(e|al)\b""", RegexOption.IGNORE_CASE),
        Regex("""\bkill\s+myself\b""", RegexOption.IGNORE_CASE),
        Regex("""\bharm\s+myself\b""", RegexOption.IGNORE_CASE),
        Regex("""\bhurt\s+myself\b""", RegexOption.IGNORE_CASE),
        Regex("""\bcutting\s+myself\b""", RegexOption.IGNORE_CASE)
    )

    private val harmToOthersPatterns: List<Regex> = listOf(
        Regex("""\b(kill|hurt|harm|destroy)\s+(him|her|them|my\s+\w+)\b""", RegexOption.IGNORE_CASE),
        Regex("""\bprayer\s+against\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(curse|smite)\s+\w+\b""", RegexOption.IGNORE_CASE)
    )

    fun screen(text: String?): ContentSafetyResult {
        if (text.isNullOrBlank()) return ContentSafetyResult.Safe

        val normalized = text.trim()

        if (selfHarmPatterns.any { it.containsMatchIn(normalized) }) {
            return ContentSafetyResult.CrisisDetected(CrisisCategory.SELF_HARM)
        }
        if (harmToOthersPatterns.any { it.containsMatchIn(normalized) }) {
            return ContentSafetyResult.CrisisDetected(CrisisCategory.HARM_TO_OTHERS)
        }
        return ContentSafetyResult.Safe
    }
}
