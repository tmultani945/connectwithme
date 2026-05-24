package com.sacredflow.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.sacredflow.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val FrauncesGoogleFont = GoogleFont("Fraunces")
private val InterGoogleFont = GoogleFont("Inter")

internal val FrauncesFamily = FontFamily(
    Font(googleFont = FrauncesGoogleFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = FrauncesGoogleFont, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(googleFont = FrauncesGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = FrauncesGoogleFont, fontProvider = provider, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(googleFont = FrauncesGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold)
)

internal val InterFamily = FontFamily(
    Font(googleFont = InterGoogleFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = InterGoogleFont, fontProvider = provider, weight = FontWeight.Bold)
)

// Material 3 ramp — used by every M3 component (buttons, chips, dialogs, etc.).
// Serif for display/headline (contemplative), sans for titles/body/labels (UI).
internal val SacredFlowMaterialTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.4).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.3).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.1).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

// Custom roles outside Material 3 — pulled by name from screens.
data class SacredTypography(
    // The prayer body — italic serif, generous leading. Used on Result & Detail.
    val prayerText: TextStyle = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 21.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.1.sp
    ),
    // Hero italic for greetings & onboarding ("Good morning, Anya.")
    val displayItalic: TextStyle = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.4).sp
    ),
    // Drop cap for the first letter of prayers.
    val dropCap: TextStyle = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 84.sp,
        lineHeight = 72.sp,
        letterSpacing = (-3).sp
    ),
    // "TO UNIVERSE" badge — small caps, heavily tracked.
    val recipientBadge: TextStyle = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 2.4.sp
    ),
    // Section overlines like "your collection", "recent reflections".
    val overline: TextStyle = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.8.sp
    ),
    // Quote / italic body — for inline prayer previews on cards.
    val quoteBody: TextStyle = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    )
)

val LocalSacredTypography = compositionLocalOf { SacredTypography() }
