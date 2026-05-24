package com.sacredflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiQuota(
    @SerialName("remainingToday") val remainingToday: Int,
    @SerialName("resetAt") val resetAt: Long
)
