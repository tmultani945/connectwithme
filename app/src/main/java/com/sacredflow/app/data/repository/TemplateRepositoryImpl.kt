package com.sacredflow.app.data.repository

import com.sacredflow.app.core.coroutines.AppDispatchers
import com.sacredflow.app.core.time.Clock
import com.sacredflow.app.data.local.dao.TemplateDao
import com.sacredflow.app.data.local.entity.Template
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val dao: TemplateDao,
    private val clock: Clock,
    private val dispatchers: AppDispatchers
) : TemplateRepository {

    override fun observeAll(): Flow<List<Template>> = dao.observeAll()

    override fun observeByUseCase(useCase: String): Flow<List<Template>> =
        dao.observeByUseCase(useCase)

    override suspend fun getById(id: Long): Template? = withContext(dispatchers.io) {
        dao.getById(id)
    }

    override suspend fun createUserTemplate(template: Template): Long = withContext(dispatchers.io) {
        // Force isBuiltIn=false on any user-created template, regardless of caller.
        dao.insert(
            template.copy(
                isBuiltIn = false,
                createdAt = if (template.createdAt == 0L) clock.nowMillis() else template.createdAt
            )
        )
    }

    override suspend fun deleteUserTemplate(id: Long) = withContext(dispatchers.io) {
        // DAO query already excludes built-in templates.
        dao.deleteUserTemplate(id)
    }
}
