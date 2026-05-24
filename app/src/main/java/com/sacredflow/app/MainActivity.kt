package com.sacredflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sacredflow.app.ui.SacredFlowApp
import com.sacredflow.app.ui.theme.SacredFlowTheme
import com.sacredflow.app.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val rootViewModel: RootViewModel = hiltViewModel()
            val themeState by rootViewModel.themeMode.collectAsStateLifecycleSafe()

            SacredFlowTheme(themeMode = themeState) {
                SacredFlowApp()
            }
        }
    }
}
