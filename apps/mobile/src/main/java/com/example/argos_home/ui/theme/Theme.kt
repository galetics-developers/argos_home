package com.example.argos_home.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The phone is designed on a light background, so the app ships a single light scheme.
// Dynamic color is deliberately off: on Android 12+ the system would override the brand palette from the wallpaper.
// One fixed scheme also keeps the phone and the watch agreeing on what every state color means.
private val BrandColorScheme = lightColorScheme(
    primary = Primary600,
    onPrimary = White50,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,
    inversePrimary = Primary300,
    secondary = Secondary600,
    onSecondary = White50,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Secondary900,
    tertiary = Tertiary600,
    onTertiary = White50,
    tertiaryContainer = Tertiary100,
    onTertiaryContainer = Tertiary900,
    background = White100,
    onBackground = Black800,
    surface = White50,
    onSurface = Black800,
    surfaceVariant = White200,
    onSurfaceVariant = Gray600,
    surfaceTint = Primary600,
    surfaceContainerLowest = White50,
    surfaceContainerLow = White100,
    surfaceContainer = White200,
    surfaceContainerHigh = White300,
    surfaceContainerHighest = White400,
    inverseSurface = Black800,
    inverseOnSurface = White100,
    outline = Gray400,
    outlineVariant = Gray200,
    error = Error1,
    onError = White50,
    errorContainer = Error4,
    onErrorContainer = Black800,
    scrim = Black1100
)

@Composable
fun Argos_homeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrandColorScheme,
        typography = Typography,
        content = content
    )
}
