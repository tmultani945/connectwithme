package com.sacredflow.app.data.repository

import com.sacredflow.app.domain.model.GenerationRequest
import com.sacredflow.app.domain.model.GenerationResult

interface GenerationRepository {
    suspend fun generate(request: GenerationRequest): GenerationResult
}
