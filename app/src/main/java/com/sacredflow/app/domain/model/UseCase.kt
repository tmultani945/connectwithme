package com.sacredflow.app.domain.model

enum class UseCase(val storageKey: String, val displayName: String, val description: String) {
    Prayer("Prayer", "Prayer", "A reverent address"),
    Intention("Intention", "Intention", "A focus for your day"),
    Gratitude("Gratitude", "Gratitude", "Thanks for what is here"),
    Healing("Healing", "Healing", "Words of comfort and care"),
    Reflection("Reflection", "Reflection", "A quiet observation"),
    Custom("Custom", "Custom", "Something else");

    companion object {
        fun fromKey(key: String): UseCase =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: Reflection
    }
}
