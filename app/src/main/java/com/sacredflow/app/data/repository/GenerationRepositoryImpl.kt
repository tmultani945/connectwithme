package com.sacredflow.app.data.repository

import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.installid.InstallIdProvider
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.fallback.FallbackContentProvider
import com.sacredflow.app.data.local.dao.GenerationHistoryDao
import com.sacredflow.app.data.local.entity.GenerationHistory
import com.sacredflow.app.data.remote.SacredFlowApi
import com.sacredflow.app.data.remote.dto.ApiGenerateRequest
import com.sacredflow.app.data.remote.dto.ApiGenerateResponse
import com.sacredflow.app.data.remote.dto.GenerationPayloadDto
import com.sacredflow.app.data.safety.ContentSafetyResult
import com.sacredflow.app.data.safety.CrisisCategory
import com.sacredflow.app.data.safety.CrisisKeywordDetector
import com.sacredflow.app.domain.model.FallbackReason
import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerationRepositoryImpl @Inject constructor(
    private val api: SacredFlowApi,
    private val installIdProvider: InstallIdProvider,
    private val fallbackProvider: FallbackContentProvider,
    private val historyDao: GenerationHistoryDao,
    private val crisisDetector: CrisisKeywordDetector,
    private val analytics: Analytics,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : GenerationRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generate(request: GenerationRequest): GenerationResult =
        withContext(dispatchers.io) {

            analytics.log(
                AnalyticsEvent.GenerationRequested(
                    useCase = request.useCase,
                    tone = request.tone,
                    length = request.length,
                    isRegeneration = request.isRegeneration
                )
            )

            // 1) Local safety pre-screen on user context. Fast, offline-capable.
            when (val safetyResult = crisisDetector.screen(request.userContext)) {
                is ContentSafetyResult.CrisisDetected -> {
                    analytics.log(AnalyticsEvent.SafetyBlocked(safetyResult.category.name))
                    logSafetyBlock(request, safetyResult.category)
                    return@withContext mapCrisisToResult(safetyResult.category)
                }
                ContentSafetyResult.Safe -> Unit
            }

            // 2) Network call with timeout.
            val started = clock.nowMillis()
            val response = runCatching {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    api.generate(buildApiRequest(request))
                }
            }

            response.fold(
                onSuccess = { mapResponse(it, request, started) },
                onFailure = { handleFailure(it, request) }
            )
        }

    private suspend fun buildApiRequest(request: GenerationRequest): ApiGenerateRequest =
        ApiGenerateRequest(
            installId = installIdProvider.get(),
            promptVersion = PROMPT_VERSION,
            isRegeneration = request.isRegeneration,
            seed = request.seed,
            request = GenerationPayloadDto(
                useCase = request.useCase,
                recipient = request.recipient,
                recipientIsCustom = request.recipientIsCustom,
                needs = request.needs,
                tone = request.tone,
                length = request.length,
                userContext = request.userContext?.takeIf { it.isNotBlank() },
                locale = request.locale
            )
        )

    private suspend fun mapResponse(
        dto: ApiGenerateResponse,
        request: GenerationRequest,
        started: Long
    ): GenerationResult = when (dto.status) {
        STATUS_OK -> handleOk(dto, request, started)
        STATUS_SOFT_BLOCKED -> {
            analytics.log(AnalyticsEvent.SafetyBlocked(dto.reason ?: "unknown"))
            logSafetyBlockFromServer(request, dto.reason)
            GenerationResult.SoftBlocked(
                category = dto.reason ?: "unknown",
                userMessage = dto.userMessage ?: DEFAULT_SOFT_BLOCK_MESSAGE
            )
        }
        STATUS_RATE_LIMITED -> GenerationResult.RateLimited(
            resetAt = dto.quota?.resetAt ?: 0L,
            userMessage = dto.userMessage ?: DEFAULT_RATE_LIMIT_MESSAGE
        )
        else -> {
            // Unknown server status → treat as failure path.
            analytics.log(AnalyticsEvent.GenerationFailed(dto.status))
            handleFailure(IllegalStateException("Unknown status: ${dto.status}"), request)
        }
    }

    private suspend fun handleOk(
        dto: ApiGenerateResponse,
        request: GenerationRequest,
        started: Long
    ): GenerationResult {
        val gen = dto.generation ?: run {
            return handleFailure(
                IllegalStateException("ok status with null generation"),
                request
            )
        }

        analytics.log(AnalyticsEvent.GenerationSucceeded(gen.latencyMs, gen.modelName))

        val historyId = historyDao.insert(
            GenerationHistory(
                prayerEntryId = null,
                inputsJson = json.encodeToString(request),
                outputText = gen.text,
                modelName = gen.modelName,
                promptVersion = gen.promptVersion,
                tokensIn = gen.tokensIn,
                tokensOut = gen.tokensOut,
                latencyMs = clock.nowMillis() - started,
                safetyFlagged = false,
                safetyReason = null,
                wasRegeneration = request.isRegeneration,
                createdAt = clock.nowMillis()
            )
        )

        return GenerationResult.Success(
            text = gen.text,
            modelName = gen.modelName,
            promptVersion = gen.promptVersion,
            latencyMs = gen.latencyMs,
            remainingFree = dto.quota?.remainingToday,
            generationHistoryId = historyId
        )
    }

    private suspend fun handleFailure(
        t: Throwable,
        request: GenerationRequest
    ): GenerationResult {
        val reason = when (t) {
            is TimeoutCancellationException -> FallbackReason.TIMEOUT
            is IOException -> FallbackReason.NETWORK
            else -> FallbackReason.UNKNOWN
        }
        analytics.log(AnalyticsEvent.GenerationFailed(reason.name))

        val fallbackText = fallbackProvider.get(request.useCase, request.tone)
        historyDao.insert(
            GenerationHistory(
                prayerEntryId = null,
                inputsJson = json.encodeToString(request),
                outputText = fallbackText,
                modelName = MODEL_NAME_FALLBACK,
                promptVersion = PROMPT_VERSION,
                tokensIn = null,
                tokensOut = null,
                latencyMs = 0L,
                safetyFlagged = false,
                safetyReason = null,
                wasRegeneration = request.isRegeneration,
                createdAt = clock.nowMillis()
            )
        )
        return GenerationResult.Fallback(text = fallbackText, reason = reason)
    }

    private fun mapCrisisToResult(category: CrisisCategory): GenerationResult.SoftBlocked =
        when (category) {
            CrisisCategory.SELF_HARM -> GenerationResult.SoftBlocked(
                category = "self_harm",
                userMessage = CRISIS_SELF_HARM_MESSAGE
            )
            CrisisCategory.HARM_TO_OTHERS -> GenerationResult.SoftBlocked(
                category = "harm_to_others",
                userMessage = CRISIS_HARM_OTHERS_MESSAGE
            )
        }

    private suspend fun logSafetyBlock(
        request: GenerationRequest,
        category: CrisisCategory
    ) {
        historyDao.insert(
            GenerationHistory(
                prayerEntryId = null,
                inputsJson = json.encodeToString(request),
                outputText = "",
                modelName = MODEL_NAME_BLOCKED,
                promptVersion = PROMPT_VERSION,
                tokensIn = null,
                tokensOut = null,
                latencyMs = 0L,
                safetyFlagged = true,
                safetyReason = category.name,
                wasRegeneration = request.isRegeneration,
                createdAt = clock.nowMillis()
            )
        )
    }

    private suspend fun logSafetyBlockFromServer(
        request: GenerationRequest,
        reason: String?
    ) {
        historyDao.insert(
            GenerationHistory(
                prayerEntryId = null,
                inputsJson = json.encodeToString(request),
                outputText = "",
                modelName = MODEL_NAME_BLOCKED,
                promptVersion = PROMPT_VERSION,
                tokensIn = null,
                tokensOut = null,
                latencyMs = 0L,
                safetyFlagged = true,
                safetyReason = reason ?: "server_unspecified",
                wasRegeneration = request.isRegeneration,
                createdAt = clock.nowMillis()
            )
        )
    }

    companion object {
        const val PROMPT_VERSION = "v1.0"
        const val REQUEST_TIMEOUT_MS = 15_000L
        const val MODEL_NAME_FALLBACK = "fallback"
        const val MODEL_NAME_BLOCKED = "blocked"

        private const val STATUS_OK = "ok"
        private const val STATUS_SOFT_BLOCKED = "soft_blocked"
        private const val STATUS_RATE_LIMITED = "rate_limited"

        private const val DEFAULT_SOFT_BLOCK_MESSAGE =
            "We couldn't generate that. Let's try a different framing."
        private const val DEFAULT_RATE_LIMIT_MESSAGE =
            "You've used today's free reflections. Connect Yourself Plus removes the limit."

        private const val CRISIS_SELF_HARM_MESSAGE =
            "What you shared sounds heavy. You don't have to carry it alone."
        private const val CRISIS_HARM_OTHERS_MESSAGE =
            "We can help you with prayers of peace, healing, and hope — but not prayers against another person."
    }
}
