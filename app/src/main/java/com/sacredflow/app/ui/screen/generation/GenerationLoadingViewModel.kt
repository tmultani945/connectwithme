package com.sacredflow.app.ui.screen.generation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerationLoadingViewModel @Inject constructor(
    val holder: GenerationResultHolder
) : ViewModel()
