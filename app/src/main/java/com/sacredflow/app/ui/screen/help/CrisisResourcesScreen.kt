package com.sacredflow.app.ui.screen.help

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sacredflow.app.data.safety.CrisisResource
import com.sacredflow.app.ui.components.BackTopBar
import com.sacredflow.app.ui.components.PrimaryButton
import com.sacredflow.app.ui.components.sacredPaper
import com.sacredflow.app.ui.theme.LocalSacredPalette

@Composable
fun CrisisResourcesScreen(
    onBack: () -> Unit,
    viewModel: CrisisResourcesViewModel = hiltViewModel()
) {
    val resources by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val palette = LocalSacredPalette.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BackTopBar(onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .sacredPaper(density = 0.4f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Please take a moment.",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.primaryInk
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "What you shared sounds heavy. You don't have to carry it alone. " +
                            "If you're in crisis or thinking about hurting yourself, please talk to someone right now.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink2
                )

                Spacer(modifier = Modifier.height(32.dp))

                resources?.regional?.forEach { resource ->
                    CrisisResourceCard(
                        resource = resource,
                        onCall = { number ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            context.startActivity(intent)
                        },
                        onSms = { number ->
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
                            context.startActivity(intent)
                        },
                        onWeb = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                resources?.global?.let { global ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.surface2),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, palette.outlineSoft)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                global.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = palette.primaryInk
                            )
                            Text(
                                global.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.ink2,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(global.url)))
                                }
                            ) {
                                Icon(Icons.Outlined.Language, contentDescription = null)
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Text("Find a helpline")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                PrimaryButton(text = "When you're ready, come back", onClick = onBack)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CrisisResourceCard(
    resource: CrisisResource,
    onCall: (String) -> Unit,
    onSms: (String) -> Unit,
    onWeb: (String) -> Unit
) {
    val palette = LocalSacredPalette.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, palette.outlineSoft)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                resource.name,
                style = MaterialTheme.typography.headlineSmall,
                color = palette.primaryInk
            )
            Text(
                resource.description,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.ink2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                resource.phone?.let {
                    FilledTonalButton(onClick = { onCall(it) }) {
                        Icon(Icons.Outlined.Phone, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Call")
                    }
                }
                resource.sms?.let {
                    FilledTonalButton(onClick = { onSms(it) }) {
                        Icon(Icons.Outlined.Sms, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Text")
                    }
                }
                resource.url?.let {
                    FilledTonalButton(onClick = { onWeb(it) }) {
                        Icon(Icons.Outlined.Language, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Web")
                    }
                }
            }
        }
    }
}
