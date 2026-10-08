package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GothicDarkColorScheme = darkColorScheme(
    primary = BloodCrimson,
    onPrimary = TextPrimary,
    primaryContainer = BloodDark,
    onPrimaryContainer = BloodGlow,
    secondary = DarkEther,
    onSecondary = VoidObsidian,
    secondaryContainer = EtherDeep,
    onSecondaryContainer = EtherGlow,
    tertiary = BrassGold,
    onTertiary = VoidObsidian,
    tertiaryContainer = BrassAged,
    onTertiaryContainer = BrassHighlight,
    background = VoidObsidian,
    onBackground = TextPrimary,
    surface = VaultSurface,
    onSurface = TextPrimary,
    surfaceVariant = VaultSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = VaultBorder,
    error = BloodCrimson,
    onError = TextPrimary
)

@Composable
fun ZulmatTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GothicDarkColorScheme,
        typography = Typography,
        content = content
    )
}
