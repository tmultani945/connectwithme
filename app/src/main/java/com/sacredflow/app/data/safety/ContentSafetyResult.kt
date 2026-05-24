package com.sacredflow.app.data.safety

sealed interface ContentSafetyResult {
    data object Safe : ContentSafetyResult
    data class CrisisDetected(val category: CrisisCategory) : ContentSafetyResult
}

enum class CrisisCategory {
    SELF_HARM,
    HARM_TO_OTHERS
}
