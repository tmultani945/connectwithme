package com.sacredflow.app.domain.model

enum class Length(
    val storageKey: String,
    val displayName: String,
    val isPlusOnly: Boolean,
    val approximateWords: String
) {
    Short("short", "Short", false, "60–100 words"),
    Medium("medium", "Medium", false, "120–180 words"),
    Long("long", "Long", true, "220–320 words");

    companion object {
        fun fromKey(key: String): Length =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: Medium
    }
}
