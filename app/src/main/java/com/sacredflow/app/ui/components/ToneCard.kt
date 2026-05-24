package com.sacredflow.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null
) {
    val palette = LocalSacredPalette.current
    Card(
        // Fixed dimensions — was heightIn(min) which let the LazyRow stretch each
        // card to fill all vertical space, and the Spacer-weight pushed the sample
        // line off-screen behind the Continue button.
        modifier = modifier
            .width(210.dp)
            .height(260.dp)
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
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = (if (selected) "SELECTED" else "TONE"),
                    style = LocalSacredTypography.current.overline,
                    color = if (selected) palette.primaryInk else palette.ink3
                )
                if (iconRes != null) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(
                            (if (selected) palette.primaryInk else palette.ink2).copy(alpha = 0.7f)
                        )
                    )
                }
            }
            Text(
                text = name,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                color = palette.ink
            )
            Text(
                text = descriptor,
                style = MaterialTheme.typography.bodySmall,
                color = palette.ink2
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "“$sampleLine”",
                style = LocalSacredTypography.current.quoteBody.copy(fontSize = 15.sp),
                color = palette.primaryInk.copy(alpha = 0.85f)
            )
        }
    }
}
