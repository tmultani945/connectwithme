package com.sacredflow.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.LocalSacredTypography
import com.sacredflow.app.ui.util.rememberSoftTap

@Composable
fun ToneCard(
    name: String,
    descriptor: String,
    sampleLine: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    @DrawableRes backgroundRes: Int? = null
) {
    val palette = LocalSacredPalette.current
    val shape = RoundedCornerShape(16.dp)
    val softTap = rememberSoftTap()
    Card(
        // Fixed dimensions — was heightIn(min) which let the LazyRow stretch each
        // card to fill all vertical space, and the Spacer-weight pushed the sample
        // line off-screen behind the Continue button.
        modifier = modifier
            .width(210.dp)
            .height(260.dp)
            .clickable { softTap(); onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundRes != null) {
                // Let the photo show through; scrim handles legibility.
                androidx.compose.ui.graphics.Color.Transparent
            } else if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) palette.primaryInk else palette.outlineSoft
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().clip(shape)) {
            if (backgroundRes != null) {
                Image(
                    painter = painterResource(backgroundRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Linen scrim — denser at the bottom so prose stays readable, lighter
                // at the top to keep the photo visible. Selected state pulls a hint
                // of the warm primary tint through.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                        .copy(alpha = 0.40f),
                                    (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                        .copy(alpha = 0.78f),
                                    (if (selected) palette.primarySoft else MaterialTheme.colorScheme.surface)
                                        .copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            }
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
}
