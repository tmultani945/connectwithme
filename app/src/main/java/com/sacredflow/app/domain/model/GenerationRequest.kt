package com.sacredflow.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerationRequest(
    val useCase: String,
    val recipient: String,
    val recipientIsCustom: Boolean,
    val needs: List<String>,
    val tone: String,
    val length: String,
    val userContext: String? = null,
    val locale: String = "en-US",
    val isRegeneration: Boolean = false,
    val seed: Long? = null
)
