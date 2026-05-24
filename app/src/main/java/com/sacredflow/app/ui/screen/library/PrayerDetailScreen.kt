package com.sacredflow.app.ui.screen.library

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import com.sacredflow.app.ui.components.Asterism
import com.sacredflow.app.ui.components.IconActionPill
import com.sacredflow.app.ui.components.RecipientBadge
import com.sacredflow.app.ui.components.SectionHeader
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.components.sacredVignette
import com.sacredflow.app.ui.practice.PracticeSheet
import com.sacredflow.app.ui.practice.rememberPracticeState
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrayerDetailScreen(
    prayerId: Long,
    onBack: () -> Unit,
    viewModel: PrayerDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val practice = rememberPracticeState(text = state.entry?.bodyText.orEmpty())
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PrayerDetailEvent.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, event.text)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
                is PrayerDetailEvent.CopiedToClipboard -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Connect Yourself", event.text))
                    snackbarHostState.showSnackbar("Copied")
                }
                is PrayerDetailEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                PrayerDetailEvent.NavigateBack -> onBack()
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
                .sacredVignette(strength = 0.08f)
        ) {
            val entry = state.entry ?: return@Box

            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar — back · recipient · trash
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.ink2
                        )
                    }
                    RecipientBadge(text = "to ${entry.recipient}")
                    IconButton(onClick = { viewModel.onAction(PrayerDetailAction.RequestDelete) }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = palette.ink2
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp)
                ) {
                    Text(
                        text = formatFullDateOverline(entry.createdAt),
                        style = typo.overline,
                        color = palette.ink3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Asterism(modifier = Modifier.align(Alignment.CenterHorizontally), size = 9.dp)
                    Spacer(modifier = Modifier.height(22.dp))

                    // Drop cap + body
                    PrayerBodyWithDropCap(entry.bodyText)

                    Spacer(modifier = Modifier.height(28.dp))
                    Asterism(modifier = Modifier.align(Alignment.CenterHorizontally), size = 9.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Metadata chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ChipPill(entry.useCase)
                        ChipPill(entry.tone)
                        entry.needs.forEach { need -> ChipPill(need) }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Private note card
                    PrivateNoteCard(
                        isEditing = state.isEditingNote,
                        existingNote = entry.userNote,
                        draft = state.noteDraft,
                        onStartEdit = { viewModel.onAction(PrayerDetailAction.StartEditingNote) },
                        onDraftChange = { viewModel.onAction(PrayerDetailAction.SetNoteDraft(it)) },
                        onSave = { viewModel.onAction(PrayerDetailAction.SaveNote) },
                        onCancel = { viewModel.onAction(PrayerDetailAction.CancelEditingNote) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Action row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconActionPill(
                        icon = if (state.isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        label = if (state.isFavorited) "Favorited" else "Favorite",
                        onClick = { viewModel.onAction(PrayerDetailAction.ToggleFavorite) },
                        tint = if (state.isFavorited) MaterialTheme.colorScheme.tertiary else palette.primaryInk
                    )
                    IconActionPill(
                        icon = Icons.Outlined.RecordVoiceOver,
                        label = "Speak after me",
                        onClick = practice::start
                    )
                    IconActionPill(
                        icon = Icons.Outlined.IosShare,
                        label = "Share",
                        onClick = { viewModel.onAction(PrayerDetailAction.Share) }
                    )
                    IconActionPill(
                        icon = Icons.Outlined.ContentCopy,
                        label = "Copy",
                        onClick = { viewModel.onAction(PrayerDetailAction.Copy) }
                    )
                }
            }

            if (state.showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { viewModel.onAction(PrayerDetailAction.CancelDelete) },
                    title = { Text("Delete this prayer?") },
                    text = { Text("You can undo this for a few seconds. After that it's gone for good.") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.onAction(PrayerDetailAction.ConfirmDelete) }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onAction(PrayerDetailAction.CancelDelete) }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            PracticeSheet(state = practice)
        }
    }
}

@Composable
private fun PrayerBodyWithDropCap(text: String) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    if (text.isBlank()) return
    val first = text.first()
    val rest = text.substring(1)
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = first.toString(),
            style = typo.dropCap,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.offset(x = (-4).dp, y = (-10).dp)
        )
        SelectionContainer(modifier = Modifier.padding(start = 44.dp)) {
            Text(
                text = rest,
                style = typo.prayerText,
                color = palette.primaryInk
            )
        }
    }
}

@Composable
private fun ChipPill(text: String) {
    val palette = LocalSacredPalette.current
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, PillShape)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = palette.ink
        )
    }
}

@Composable
private fun PrivateNoteCard(
    isEditing: Boolean,
    existingNote: String?,
    draft: String,
    onStartEdit: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface2, RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.StickyNote2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SectionHeader("Private note")
                }
                if (!isEditing) {
                    IconButton(onClick = onStartEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit note", tint = palette.ink3)
                    }
                }
            }
            if (isEditing) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    placeholder = { Text("Add a private note (just for you).") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    PrimaryButton(text = "Save", onClick = onSave, modifier = Modifier.weight(1f, fill = false))
                }
            } else {
                Text(
                    text = existingNote?.takeIf { it.isNotBlank() } ?: "Add a private note.",
                    style = typo.quoteBody,
                    color = if (existingNote.isNullOrBlank()) palette.ink3 else palette.ink2
                )
            }
        }
    }
}

private fun formatFullDateOverline(epochMillis: Long): String =
    SimpleDateFormat("MMM d · h:mm a", Locale.getDefault()).format(Date(epochMillis)).uppercase()
