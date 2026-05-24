package com.sacredflow.app.domain.model

enum class Need(val storageKey: String, val displayName: String) {
    Peace("peace", "Peace"),
    Healing("healing", "Healing"),
    Clarity("clarity", "Clarity"),
    Gratitude("gratitude", "Gratitude"),
    Strength("strength", "Strength"),
    Hope("hope", "Hope"),
    Success("success", "Success"),
    Abundance("abundance", "Abundance"),
    Forgiveness("forgiveness", "Forgiveness"),
    Protection("protection", "Protection"),
    Joy("joy", "Joy"),
    Courage("courage", "Courage");

    companion object {
        const val MAX_SELECTABLE = 2

        fun fromKey(key: String): Need? =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) }

        fun fromKeys(keys: List<String>): Set<Need> =
            keys.mapNotNull { fromKey(it) }.toSet()
    }
}
