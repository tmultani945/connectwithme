package com.sacredflow.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sacredflow.app.ui.navigation.HomeRoute
import com.sacredflow.app.ui.navigation.LibraryRoute
import com.sacredflow.app.ui.navigation.ReminderRoute
import com.sacredflow.app.ui.navigation.SettingsRoute
import com.sacredflow.app.ui.theme.LocalSacredPalette

private data class BottomItem(
    val routeSimpleName: String,
    val route: Any,
    val label: String,
    val icon: ImageVector
)

private val items = listOf(
    BottomItem(HomeRoute::class.simpleName!!, HomeRoute, "Home", Icons.Outlined.WbSunny),
    BottomItem(LibraryRoute::class.simpleName!!, LibraryRoute, "Library", Icons.Outlined.AutoStories),
    BottomItem(ReminderRoute::class.simpleName!!, ReminderRoute, "Reminders", Icons.Outlined.NotificationsNone),
    BottomItem(SettingsRoute::class.simpleName!!, SettingsRoute, "Settings", Icons.Outlined.Tune)
)

@Composable
fun SacredBottomBar(
    currentRoute: String?,
    onSelect: (Any) -> Unit
) {
    val palette = LocalSacredPalette.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.routeSimpleName
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = palette.primaryInk,
                    selectedTextColor = palette.primaryInk,
                    indicatorColor = palette.primarySoft,
                    unselectedIconColor = palette.ink3,
                    unselectedTextColor = palette.ink3
                )
            )
        }
    }
}
