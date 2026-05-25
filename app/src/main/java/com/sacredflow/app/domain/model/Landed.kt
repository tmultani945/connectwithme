package com.sacredflow.app.domain.model

/**
 * The user's "how did this land?" response on a generated reflection.
 *
 * Three options, no in-betweens — enough nuance to capture whether a prayer
 * was useful without becoming a heavy 5-point rating burden. Stored as
 * [storageKey] on PrayerEntry.landed.
 */
enum class Landed(
    val storageKey: String,
    val displayName: String
) {
    Steady("steady", "Kept me steady"),
    Okay("okay", "It was okay"),
    Missed("missed", "Didn't quite land");

    companion object {
        fun fromKey(key: String?): Landed? =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) }
    }
}
