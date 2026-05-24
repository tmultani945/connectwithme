package com.sacredflow.app.data.repository

import com.sacredflow.app.data.local.entity.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun observeAll(): Flow<List<Template>>
    fun observeByUseCase(useCase: String): Flow<List<Template>>
    suspend fun getById(id: Long): Template?
    suspend fun createUserTemplate(template: Template): Long
    suspend fun deleteUserTemplate(id: Long)
}
