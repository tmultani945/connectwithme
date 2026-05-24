package com.sacredflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiGenerateResponse(
    @SerialName("status") val status: String,
    @SerialName("generation") val generation: GenerationDto? = null,
    @SerialName("quota") val quota: ApiQuota? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("userMessage") val userMessage: String? = null,
    @SerialName("routeTo") val routeTo: String? = null
)

@Serializable
data class GenerationDto(
    @SerialName("text") val text: String,
    @SerialName("modelName") val modelName: String,
    @SerialName("promptVersion") val promptVersion: String,
    @SerialName("tokensIn") val tokensIn: Int? = null,
    @SerialName("tokensOut") val tokensOut: Int? = null,
    @SerialName("latencyMs") val latencyMs: Long
)
