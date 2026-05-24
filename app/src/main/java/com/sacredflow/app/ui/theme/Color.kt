package com.sacredflow.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Light ───────────────────────────────────────────────
private val LightPrimary = Color(0xFF7C6F5A)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFEFE6D2)
private val LightOnPrimaryContainer = Color(0xFF2E2818)

private val LightSecondary = Color(0xFF8DA48E)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFE2EBE0)
private val LightOnSecondaryContainer = Color(0xFF1F2D20)

private val LightTertiary = Color(0xFFB98970)
private val LightOnTertiary = Color(0xFFFFFFFF)

private val LightBackground = Color(0xFFFAF7F1)
private val LightSurface = Color(0xFFFFFDF8)
private val LightSurfaceVariant = Color(0xFFEDE7DA)
private val LightOnBackground = Color(0xFF2A2419)
private val LightOnSurface = Color(0xFF2A2419)
private val LightOnSurfaceVariant = Color(0xFF5C5240)
private val LightOutline = Color(0xFFB5AB95)
private val LightError = Color(0xFF9C5A4E)
private val LightOnError = Color(0xFFFFFFFF)

// ── Dark ────────────────────────────────────────────────
private val DarkPrimary = Color(0xFFD7C8AE)
private val DarkOnPrimary = Color(0xFF3A301E)
private val DarkPrimaryContainer = Color(0xFF3A3122)
private val DarkOnPrimaryContainer = Color(0xFFF3EBD8)

private val DarkSecondary = Color(0xFFB6CCB6)
private val DarkOnSecondary = Color(0xFF1F2D20)
private val DarkSecondaryContainer = Color(0xFF2C3A2C)
private val DarkOnSecondaryContainer = Color(0xFFE2EBE0)

private val DarkTertiary = Color(0xFFE0B498)
private val DarkOnTertiary = Color(0xFF3A2418)

private val DarkBackground = Color(0xFF14110C)
private val DarkSurface = Color(0xFF1A1610)
private val DarkSurfaceVariant = Color(0xFF2A2419)
private val DarkOnBackground = Color(0xFFF3EBD8)
private val DarkOnSurface = Color(0xFFF3EBD8)
private val DarkOnSurfaceVariant = Color(0xFFC7BCA1)
private val DarkOutline = Color(0xFF5C5240)
private val DarkError = Color(0xFFE59A8C)
private val DarkOnError = Color(0xFF3A1A14)

internal val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightError,
    onError = LightOnError
)

internal val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = DarkError,
    onError = DarkOnError
)

// ── Extended brand palette (not in Material 3) ──────────
// Exposes the warm-letterpress design tokens the rest of the app pulls from.
data class SacredPalette(
    val surface2: Color,        // warm beige sub-surface
    val outlineSoft: Color,     // softer than Material outline; used for chips, cards
    val primaryInk: Color,      // deep brown — the "ink" color for headlines & primary buttons
    val primarySoft: Color,     // warm beige container
    val secondarySoft: Color,   // light sage container
    val tertiarySoft: Color,    // warm peach container
    val ink: Color,             // primary text color (warmer than onSurface)
    val ink2: Color,            // secondary text
    val ink3: Color             // tertiary / hint text
)

internal val LightSacredPalette = SacredPalette(
    surface2 = Color(0xFFF3EDDF),
    outlineSoft = Color(0xFFD9D1BD),
    primaryInk = Color(0xFF3D3527),
    primarySoft = Color(0xFFEFE6D2),
    secondarySoft = Color(0xFFE2EBE0),
    tertiarySoft = Color(0xFFF1E0D2),
    ink = Color(0xFF2A2419),
    ink2 = Color(0xFF5C5240),
    ink3 = Color(0xFF8B8167)
)

internal val DarkSacredPalette = SacredPalette(
    surface2 = Color(0xFF221C13),
    outlineSoft = Color(0xFF3A3122),
    primaryInk = Color(0xFFF3EBD8),
    primarySoft = Color(0xFF3A3122),
    secondarySoft = Color(0xFF2C3A2C),
    tertiarySoft = Color(0xFF3A2C20),
    ink = Color(0xFFF3EBD8),
    ink2 = Color(0xFFC7BCA1),
    ink3 = Color(0xFF8B8167)
)

val LocalSacredPalette = compositionLocalOf { LightSacredPalette }
