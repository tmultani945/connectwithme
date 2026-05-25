package com.sacredflow.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.theme.LocalSacredPalette
import com.sacredflow.app.ui.theme.PillShape
import com.sacredflow.app.ui.util.rememberFirmTap

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val palette = LocalSacredPalette.current
    val firmTap = rememberFirmTap()
    Button(
        onClick = { firmTap(); onClick() },
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.primaryInk,
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = palette.outlineSoft,
            disabledContentColor = palette.ink3
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.background,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
