package com.sacredflow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography

@Composable
fun ToneCard(
    name: String,
    descriptor: String,
    sampleLine: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    Card(
        modifier = modifier
            .width(230.dp)
            .heightIn(min = 220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            if (selected) palette.primaryInk.copy(alpha = 0.5f) else palette.outlineSoft
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp).heightIn(min = 200.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = (if (selected) "SELECTED" else "TONE"),
                style = LocalSacredTypography.current.overline,
                color = if (selected) palette.primaryInk else palette.ink3
            )
            Text(
                text = name,
                style = MaterialTheme.typography.displaySmall,
                color = palette.ink
            )
            Text(
                text = descriptor,
                style = MaterialTheme.typography.bodySmall,
                color = palette.ink2
            )
            Spacer(modifier = Modifier.weight(1f, fill = true))
            Text(
                text = "“$sampleLine”",
                style = LocalSacredTypography.current.quoteBody.copy(fontSize = 17.sp),
                color = palette.primaryInk.copy(alpha = 0.85f)
            )
        }
    }
}
