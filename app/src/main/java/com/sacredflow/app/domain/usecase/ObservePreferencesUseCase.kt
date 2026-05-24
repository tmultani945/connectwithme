package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.local.entity.UserPreference
import com.sacredflow.app.data.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePreferencesUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke(): Flow<UserPreference> = preferenceRepository.observe()
}
