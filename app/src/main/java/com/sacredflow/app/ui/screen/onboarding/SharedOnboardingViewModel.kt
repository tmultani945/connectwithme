package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.sacredflow.app.ui.navigation.OnboardingGraph

@Composable
fun sharedOnboardingViewModel(navController: NavController): OnboardingViewModel {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry(OnboardingGraph)
    }
    return hiltViewModel(parentEntry)
}
