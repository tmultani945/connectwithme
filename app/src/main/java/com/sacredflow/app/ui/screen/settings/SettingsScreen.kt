package com.sacredflow.app.ui.screen.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenPaywall: () -> Unit,
    onOpenHelp: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showThemeSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState()
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SettingsEvent.OpenUrl -> {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(event.url)
                    )
                    context.startActivity(intent)
                }
                is SettingsEvent.ExportText -> Unit
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.5f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 18.dp, bottom = 14.dp)) {
                        Text("PREFERENCES", style = typo.overline, color = palette.ink3)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.displayMedium,
                            color = palette.primaryInk
                        )
                    }
                }

                item { SectionHeader("Appearance") }
                item {
                    SettingsRow(
                        title = "Theme",
                        subtitle = when (state.themeMode) {
                            ThemeMode.System -> "Follow system"
                            ThemeMode.Light -> "Light"
                            ThemeMode.Dark -> "Dark"
                        },
                        onClick = { showThemeSheet = true }
                    )
                }
                item {
                    SettingsRowSwitch(
                        title = "Use system color (Material You)",
                        subtitle = "Android 12+",
                        checked = state.useDynamicColor,
                        onCheckedChange = {
                            viewModel.onAction(SettingsAction.SetUseDynamicColor(it))
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("Subscription") }
                item {
                    SettingsRow(
                        title = if (state.isPlusSubscriber) "Connect Yourself Plus" else "Free plan",
                        subtitle = if (state.isPlusSubscriber) "Tap to manage" else "Upgrade for unlimited reflections",
                        onClick = onOpenPaywall
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("Library") }
                item {
                    SettingsRow(
                        title = "Saved reflections",
                        subtitle = "${state.totalSavedCount} total",
                        onClick = { },
                        showChevron = false
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("About") }
                item { SettingsRow(title = "Help & disclaimer", onClick = onOpenHelp) }
                item {
                    SettingsRow(
                        title = "Privacy policy",
                        onClick = { viewModel.onAction(SettingsAction.RequestClearData) }
                    )
                }
                item {
                    SettingsRow(
                        title = "Version",
                        subtitle = state.appVersion,
                        onClick = { },
                        showChevron = false
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        if (showThemeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showThemeSheet = false },
                sheetState = themeSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Theme",
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onAction(SettingsAction.SetThemeMode(mode))
                                    showThemeSheet = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.themeMode == mode,
                                onClick = {
                                    viewModel.onAction(SettingsAction.SetThemeMode(mode))
                                    showThemeSheet = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = when (mode) {
                                    ThemeMode.System -> "Follow system"
                                    ThemeMode.Light -> "Light"
                                    ThemeMode.Dark -> "Dark"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.ink
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showChevron: Boolean = true
) {
    val palette = LocalSacredPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showChevron, onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.ink)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.ink3
                )
            }
        }
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = palette.ink3
            )
        }
    }
    HorizontalDivider(color = palette.outlineSoft.copy(alpha = 0.4f))
}

@Composable
private fun SettingsRowSwitch(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = LocalSacredPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.ink)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.ink3
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider(color = palette.outlineSoft.copy(alpha = 0.4f))
}
