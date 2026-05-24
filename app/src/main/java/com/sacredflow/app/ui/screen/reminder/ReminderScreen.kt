package com.sacredflow.app.ui.screen.reminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.RuleLine
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

// ISO weekday numbering: Monday = 1 … Sunday = 7
private val EVERY_DAY: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7)
private val WEEKDAYS: Set<Int> = setOf(1, 2, 3, 4, 5)
private val WEEKENDS: Set<Int> = setOf(6, 7)
private val DAY_LABELS: List<Pair<Int, String>> = listOf(
    1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onAction(ReminderAction.SetPermissionGranted(granted))
        if (granted) viewModel.onAction(ReminderAction.Save)
    }

    // Initial permission check.
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

    var showTimePicker by remember { mutableStateOf(false) }
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
                    .padding(horizontal = 28.dp)
                    .padding(top = 24.dp, bottom = 24.dp),
            ) {
                // ── Hero: overline + huge time + summary ──
                Text(
                    text = "DAILY REMINDER",
                    style = typo.overline,
                    color = palette.ink3
                )
                Spacer(modifier = Modifier.height(20.dp))

                HeroTimeBlock(
                    hour = state.hour,
                    minute = state.minute,
                    daysOfWeek = state.daysOfWeek,
                    onTapTime = { showTimePicker = true }
                )

                Spacer(modifier = Modifier.height(36.dp))
                RuleLine(ornament = true)
                Spacer(modifier = Modifier.height(28.dp))

                // ── Day presets ──
                DayPresetRow(
                    selected = state.daysOfWeek,
                    onPick = { preset -> viewModel.onAction(ReminderAction.SetDays(preset)) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Day strip ──
                DayStrip(
                    selected = state.daysOfWeek,
                    onToggle = { iso -> viewModel.onAction(ReminderAction.ToggleDay(iso)) }
                )

                Spacer(modifier = Modifier.height(32.dp))
                RuleLine(ornament = false)
                Spacer(modifier = Modifier.height(20.dp))

                // ── Notification preview ──
                Text(
                    text = "\"A quiet moment for you.\"",
                    style = typo.quoteBody.copy(fontSize = 18.sp),
                    color = palette.primaryInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "you'll see this at ${formatTime(state.hour, state.minute)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = palette.ink3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Permission rationale ──
                AnimatedVisibility(visible = state.needsNotificationPermission) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.surface2, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "We'll ask for notification permission when you tap Save.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.ink2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // ── Save CTA ──
                PrimaryButton(
                    text = if (state.existingId == null) "Save reminder" else "Save changes",
                    onClick = { viewModel.onAction(ReminderAction.Save) },
                    enabled = state.canSave,
                    isLoading = state.isSaving
                )

                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { viewModel.onAction(ReminderAction.TestNow) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send a test notification now", color = palette.ink3)
                }

                if (state.existingId != null) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove reminder", color = palette.ink3)
                    }
                }
            }
        }

        // ── Time picker sheet ──
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = state.hour,
                initialMinute = state.minute,
                is24Hour = false
            )
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.onAction(ReminderAction.SetTime(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "When?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.primaryInk
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Set",
                        onClick = {
                            viewModel.onAction(ReminderAction.SetTime(timePickerState.hour, timePickerState.minute))
                            showTimePicker = false
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // ── Delete confirm ──
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

// ─────────────────────────────────────────────────────────────────────────────
// Hero: huge italic time + summary line. Tap to edit.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeroTimeBlock(
    hour: Int,
    minute: Int,
    daysOfWeek: Set<Int>,
    onTapTime: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val (timeText, meridiem) = format12hParts(hour, minute)
    val summary = summarizeDays(daysOfWeek)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTapTime() }
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 86.sp, lineHeight = 86.sp),
                color = palette.primaryInk
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = meridiem,
                style = MaterialTheme.typography.displaySmall.copy(fontStyle = FontStyle.Italic),
                color = palette.ink2,
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summary,
            style = typo.overline,
            color = palette.ink3
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Day preset row: Every day / Weekdays / Weekends
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DayPresetRow(
    selected: Set<Int>,
    onPick: (Set<Int>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PresetPill(
            label = "Every day",
            selected = selected == EVERY_DAY,
            onClick = { onPick(EVERY_DAY) },
            modifier = Modifier.weight(1f)
        )
        PresetPill(
            label = "Weekdays",
            selected = selected == WEEKDAYS,
            onClick = { onPick(WEEKDAYS) },
            modifier = Modifier.weight(1f)
        )
        PresetPill(
            label = "Weekends",
            selected = selected == WEEKENDS,
            onClick = { onPick(WEEKENDS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PresetPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) palette.primaryInk else palette.ink2
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Day strip: 7 small toggleable circles M T W T F S S
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DayStrip(
    selected: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val palette = LocalSacredPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DAY_LABELS.forEach { (iso, label) ->
            val on = iso in selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (on) palette.primaryInk else Color.Transparent,
                        CircleShape
                    )
                    .let { mod ->
                        if (!on) mod.background(MaterialTheme.colorScheme.surface, CircleShape) else mod
                    }
                    .clickable { onToggle(iso) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (on) MaterialTheme.colorScheme.background else palette.ink
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Format helpers
// ─────────────────────────────────────────────────────────────────────────────
private fun format12hParts(hour: Int, minute: Int): Pair<String, String> {
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val mm = minute.toString().padStart(2, '0')
    val meridiem = if (hour < 12) "am" else "pm"
    return "$h12:$mm" to meridiem
}

private fun formatTime(hour: Int, minute: Int): String {
    val (t, m) = format12hParts(hour, minute)
    return "$t $m"
}

private fun summarizeDays(days: Set<Int>): String = when {
    days.isEmpty() -> "NO DAYS SELECTED"
    days == EVERY_DAY -> "EVERY DAY"
    days == WEEKDAYS -> "MONDAY TO FRIDAY"
    days == WEEKENDS -> "WEEKENDS"
    else -> days.sorted().joinToString(" · ") { isoToShort(it) }.uppercase()
}

private fun isoToShort(iso: Int): String = when (iso) {
    1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"
    5 -> "Fri"; 6 -> "Sat"; 7 -> "Sun"
    else -> "?"
}
