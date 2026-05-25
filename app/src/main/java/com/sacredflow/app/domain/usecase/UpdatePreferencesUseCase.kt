package com.sacredflow.app.domain.usecase

import com.sacredflow.app.data.local.entity.UserPreference
import com.sacredflow.app.data.repository.PreferenceRepository
import com.sacredflow.app.ui.theme.ThemeMode
import javax.inject.Inject

class UpdatePreferencesUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(transform: (UserPreference) -> UserPreference) {
        preferenceRepository.update(transform)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        preferenceRepository.setThemeMode(mode)
    }

    suspend fun addCustomRecipient(name: String) {
        preferenceRepository.addCustomRecipient(name)
    }

    suspend fun setVoiceKey(key: String) {
        preferenceRepository.update { it.copy(voiceKey = key) }
    }
}
