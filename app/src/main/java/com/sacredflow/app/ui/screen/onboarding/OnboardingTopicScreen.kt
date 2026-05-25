package com.sacredflow.app.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.TopicSuggestions as TOPIC_SUGGESTIONS

@Composable
fun OnboardingTopicScreen(
    navController: NavController,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = sharedOnboardingViewModel(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val suggestionsState = rememberLazyListState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack, sub = "step 2 of 4") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
                .padding(horizontal = 28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "What is this about?",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "The topic, in your words.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )
                Spacer(modifier = Modifier.height(28.dp))

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Text("TRY ONE", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        state = suggestionsState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(TOPIC_SUGGESTIONS) { suggestion ->
                            SuggestionChip(
                                label = suggestion,
                                selected = state.topic == suggestion,
                                onClick = { viewModel.onAction(OnboardingAction.SetTopic(suggestion)) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = state.topic,
                        onValueChange = { viewModel.onAction(OnboardingAction.SetTopic(it)) },
                        placeholder = {
                            Text(
                                "Write in your own words…",
                                color = palette.ink3,
                                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Be as specific or open as you like.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.ink3
                        )
                        Text(
                            text = "${state.topic.length} / ${OnboardingState.MAX_TOPIC_LENGTH}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.ink3
                        )
                    }
                }

                PrimaryButton(
                    text = "Continue",
                    onClick = onNext,
                    enabled = state.canContinueFromTopic,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalSacredPalette.current
    Box(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
            .background(
                if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface,
                PillShape
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) palette.primaryInk else palette.ink2
        )
    }
}
