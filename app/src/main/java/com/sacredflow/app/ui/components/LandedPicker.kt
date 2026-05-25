package com.sacredflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacredflow.app.domain.model.Landed
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.rememberSoftTap

/**
 * A quiet three-option "how did this land?" reflection shown after a generated
 * prayer. Tapping a pill records the response. Tapping the selected one again
 * clears it (so the user can recant or revisit).
 *
 * Renders nothing more than a small overline + a row of 3 pills. No fireworks.
 */
@Composable
fun LandedPicker(
    selected: Landed?,
    onSelect: (Landed?) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    val typo = LocalSacredTypography.current
    val softTap = rememberSoftTap()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "HOW DID THIS LAND?",
            style = typo.overline.copy(letterSpacing = 3.sp),
            color = palette.ink3,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Landed.entries.forEach { option ->
                LandedPill(
                    option = option,
                    selected = selected == option,
                    onClick = {
                        softTap()
                        onSelect(if (selected == option) null else option)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LandedPill(
    option: Landed,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSacredPalette.current
    Box(
        // Fixed 52dp height so all three pills line up the same regardless of
        // whether their labels wrap. Labels like "Didn't quite land" don't fit
        // on one line at 1/3 of the screen width — let them wrap to two.
        modifier = modifier
            .height(52.dp)
            .clickable { onClick() }
            .background(
                if (selected) palette.primaryInk else MaterialTheme.colorScheme.background,
                PillShape
            )
            .border(
                width = 1.dp,
                color = if (selected) palette.primaryInk else palette.outlineSoft,
                shape = PillShape
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option.displayName,
            style = MaterialTheme.typography.labelMedium.copy(lineHeight = 16.sp),
            color = if (selected) MaterialTheme.colorScheme.background else palette.ink,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

