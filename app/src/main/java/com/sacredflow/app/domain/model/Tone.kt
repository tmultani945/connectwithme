package com.sacredflow.app.domain.model

enum class Tone(
    val storageKey: String,
    val displayName: String,
    val descriptor: String,
    val sampleLine: String
) {
    Gentle("gentle", "Gentle", "soft and tender",
        "May the morning meet you kindly."),
    Hopeful("hopeful", "Hopeful", "looking forward",
        "Something good is on its way."),
    Thankful("thankful", "Thankful", "grateful and warm",
        "For this breath, thank you."),
    Grounded("grounded", "Grounded", "steady and clear",
        "My feet are on the floor. I am here."),
    Powerful("powerful", "Powerful", "strong and resolute",
        "I will not be smaller than I am."),
    Surrendering("surrendering", "Surrendering", "releasing and open",
        "I let go of what is not mine to carry.");

    companion object {
        fun fromKey(key: String): Tone =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: Gentle
    }
}
