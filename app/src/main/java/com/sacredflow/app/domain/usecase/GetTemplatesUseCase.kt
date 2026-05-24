package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.local.entity.Template
import com.sacredflow.app.data.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository
) {
    operator fun invoke(useCaseFilter: String? = null): Flow<List<Template>> =
        if (useCaseFilter == null) templateRepository.observeAll()
        else templateRepository.observeByUseCase(useCaseFilter)
}
