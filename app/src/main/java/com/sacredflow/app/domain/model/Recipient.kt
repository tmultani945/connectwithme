package com.sacredflow.app.domain.model

sealed interface Recipient {
    val displayName: String
    val isCustom: Boolean

    data class BuiltIn(override val displayName: String) : Recipient {
        override val isCustom: Boolean = false
    }

    data class Custom(override val displayName: String) : Recipient {
        override val isCustom: Boolean = true
    }

    companion object {
        val BuiltInOptions: List<BuiltIn> = listOf(
            BuiltIn("God"),
            BuiltIn("Universe"),
            BuiltIn("Nature"),
            BuiltIn("Higher Self"),
            BuiltIn("Divine Energy"),
            BuiltIn("Ancestors"),
            BuiltIn("The Sacred")
        )

        fun fromStorage(name: String, isCustom: Boolean): Recipient =
            if (isCustom) Custom(name) else BuiltIn(name)

        /**
         * Heuristic when only the stored name is available (no boolean flag).
         */
        fun fromName(name: String): Recipient =
            BuiltInOptions.firstOrNull { it.displayName == name } ?: Custom(name)
    }
}
