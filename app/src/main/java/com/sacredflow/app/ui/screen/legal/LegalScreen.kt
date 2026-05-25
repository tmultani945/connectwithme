package com.sacredflow.app.ui.screen.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

/**
 * Reusable scaffold for the static legal pages (Privacy, Terms). Renders a
 * heading, last-updated stamp, and a sequence of (heading, body) sections.
 *
 * The text content below is a working draft that captures the intent. A lawyer
 * should review before public launch.
 */
@Composable
fun LegalScreen(
    title: String,
    lastUpdated: String,
    sections: List<Pair<String, String>>,
    onBack: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(title = title, onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Last updated · $lastUpdated",
                    style = typo.overline,
                    color = palette.ink3
                )
                Spacer(modifier = Modifier.height(24.dp))

                sections.forEachIndexed { index, (heading, body) ->
                    if (index > 0) Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = heading,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.ink
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.ink2
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Questions? hello@connectyourself.app",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = palette.ink3
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
