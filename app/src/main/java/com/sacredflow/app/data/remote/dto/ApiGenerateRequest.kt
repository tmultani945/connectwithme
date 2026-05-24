package com.sacredflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiGenerateRequest(
    @SerialName("installId") val installId: String,
    @SerialName("promptVersion") val promptVersion: String,
    @SerialName("request") val request: GenerationPayloadDto,
    @SerialName("isRegeneration") val isRegeneration: Boolean,
    @SerialName("seed") val seed: Long? = null
)

@Serializable
data class GenerationPayloadDto(
    @SerialName("useCase") val useCase: String,
    @SerialName("recipient") val recipient: String,
    @SerialName("recipientIsCustom") val recipientIsCustom: Boolean,
    @SerialName("needs") val needs: List<String>,
    @SerialName("tone") val tone: String,
    @SerialName("length") val length: String,
    @SerialName("userContext") val userContext: String? = null,
    @SerialName("locale") val locale: String = "en-US"
)
