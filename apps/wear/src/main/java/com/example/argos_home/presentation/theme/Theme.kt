package com.example.argos_home.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

// The watch is designed on a near black background, so the app ships a single dark scheme.
// Dynamic color is deliberately off: it would derive the scheme from the active watch face and override the brand.
// This is the watch ColorScheme, not the mobile one: it has its own slots (no surface, scrim or inverse ones).
private val BrandColorScheme = ColorScheme(
    // A lighter tint than the phone accent: over a near black background primary-600 reads as almost flat.
    primary = Primary400,
    primaryDim = Primary500,
    primaryContainer = Primary800,
    onPrimary = Primary1100,
    onPrimaryContainer = Primary100,
    secondary = Secondary400,
    secondaryDim = Secondary500,
    secondaryContainer = Secondary800,
    onSecondary = Secondary1100,
    onSecondaryContainer = Secondary100,
    tertiary = Tertiary400,
    tertiaryDim = Tertiary500,
    tertiaryContainer = Tertiary800,
    onTertiary = Tertiary1100,
    onTertiaryContainer = Tertiary100,
    surfaceContainerLow = Black950,
    surfaceContainer = Black900,
    surfaceContainerHigh = Black700,
    onSurface = White50,
    onSurfaceVariant = Gray400,
    outline = Gray600,
    outlineVariant = Gray700,
    background = Black1100,
    onBackground = White50,
    error = Error1,
    // The error family has no tone darker than error-1, so the dimmed variant reuses the darkest red available.
    errorDim = Secondary800,
    errorContainer = Secondary950,
    onError = White50,
    onErrorContainer = Error4
)

@Composable
fun Argos_homeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrandColorScheme,
        content = content
    )
}
