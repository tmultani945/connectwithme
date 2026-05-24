package com.sacredflow.app.domain.model

sealed interface GenerationResult {

    data class Success(
        val text: String,
        val modelName: String,
        val promptVersion: String,
        val latencyMs: Long,
        val remainingFree: Int?,
        val generationHistoryId: Long
    ) : GenerationResult

    data class Fallback(
        val text: String,
        val reason: FallbackReason
    ) : GenerationResult

    data class SoftBlocked(
        val category: String,
        val userMessage: String
    ) : GenerationResult

    data class RateLimited(
        val resetAt: Long,
        val userMessage: String
    ) : GenerationResult

    data class Error(
        val userMessage: String
    ) : GenerationResult
}

enum class FallbackReason {
    NETWORK,
    UPSTREAM,
    TIMEOUT,
    UNKNOWN
}
