package com.sacredflow.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sacredflow.app.ui.components.SacredAppBackdrop
import com.sacredflow.app.ui.navigation.SacredFlowNavHost

@Composable
fun SacredFlowApp() {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {
        // App-level backdrop — sunrise photo + warm gradient, visible behind every screen.
        // Per-screen sacredPaper() and TimeOfDayBackdrop modifiers layer on top of this.
        SacredAppBackdrop()
        // NavHost is transparent — every Scaffold uses Color.Transparent so the
        // backdrop reads through.
        SacredFlowNavHost(navController = navController)
    }
}
