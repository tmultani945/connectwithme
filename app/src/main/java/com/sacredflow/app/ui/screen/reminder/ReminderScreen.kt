package com.sacredflow.app.ui.screen.reminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.ui.components.DenseChip
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    val timePickerState = rememberTimePickerState(
        initialHour = state.hour,
        initialMinute = state.minute,
        is24Hour = false
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        if (state.isLoaded && (timePickerState.hour != state.hour || timePickerState.minute != state.minute)) {
            viewModel.onAction(ReminderAction.SetTime(timePickerState.hour, timePickerState.minute))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onAction(ReminderAction.SetPermissionGranted(granted))
        if (granted) viewModel.onAction(ReminderAction.Save)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.onAction(ReminderAction.SetPermissionGranted(granted))
        } else {
            viewModel.onAction(ReminderAction.SetPermissionGranted(true))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ReminderEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                ReminderEvent.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(modifier = Modifier.padding(top = 18.dp)) {
                    Text("REMINDERS", style = typo.overline, color = palette.ink3)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Daily reflection",
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A gentle daily nudge can help build the practice.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.ink2
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enabled", style = MaterialTheme.typography.titleMedium, color = palette.ink)
                    Switch(
                        checked = state.isEnabled,
                        onCheckedChange = { viewModel.onAction(ReminderAction.SetEnabled(it)) }
                    )
                }

                SectionHeader("Time")
                TimePicker(state = timePickerState)

                SectionHeader("Days")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daysOfWeekLabels.forEach { (iso, label) ->
                        DenseChip(
                            label = label,
                            selected = iso in state.daysOfWeek,
                            onClick = { viewModel.onAction(ReminderAction.ToggleDay(iso)) }
                        )
                    }
                }

                if (state.isEnabled && state.needsNotificationPermission) {
                    Text(
                        text = "Connect Yourself needs notification permission to send your daily reminder.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.ink2
                    )
                }

                if (state.existingId != null) {
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Text("Remove reminder", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                PrimaryButton(
                    text = if (state.existingId == null) "Set reminder" else "Save changes",
                    onClick = { viewModel.onAction(ReminderAction.Save) },
                    enabled = state.canSave,
                    isLoading = state.isSaving
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Remove the reminder?") },
                text = { Text("You can always set it up again.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        viewModel.onAction(ReminderAction.Delete)
                    }) { Text("Remove") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                }
            )
        }
    }
}

private val daysOfWeekLabels = listOf(
    1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
    5 to "Fri", 6 to "Sat", 7 to "Sun"
)
