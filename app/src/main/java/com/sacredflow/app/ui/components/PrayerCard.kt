package com.sacredflow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrayerCard(
    recipient: String,
    bodyPreview: String,
    createdAt: Long,
    isFavorited: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    accentColor: Color? = null,
    wide: Boolean = true
) {
    val palette = LocalSacredPalette.current
    val accent = accentColor ?: palette.primaryInk
    Card(
        modifier = modifier
            .let { if (wide) it.fillMaxWidth() else it.widthIn(min = 260.dp, max = 280.dp) }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, palette.outlineSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left hairline accent
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 14.dp)
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(accent.copy(alpha = 0.55f), RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .padding(end = 20.dp, top = 18.dp, bottom = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TO $recipient".uppercase(),
                        style = LocalSacredTypography.current.recipientBadge,
                        color = palette.primaryInk
                    )
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorited) "Favorited" else null,
                        tint = if (isFavorited) MaterialTheme.colorScheme.tertiary else palette.ink3,
                        modifier = Modifier.height(16.dp)
                    )
                }
                Text(
                    text = bodyPreview.replace('\n', ' '),
                    style = LocalSacredTypography.current.quoteBody,
                    color = palette.ink,
                    maxLines = if (wide) 2 else 4,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDate(createdAt).uppercase(),
                    style = LocalSacredTypography.current.overline,
                    color = palette.ink3
                )
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMillis
    val day = 24L * 60L * 60L * 1000L
    return when {
        diff < day -> "Today"
        diff < 2 * day -> "Yesterday"
        diff < 7 * day -> "${diff / day} days ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(epochMillis))
    }
}
