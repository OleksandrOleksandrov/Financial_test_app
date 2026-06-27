package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PolishedPrimaryDark,
    onPrimary = PolishedOnPrimaryDark,
    primaryContainer = PolishedPrimaryContainerDark,
    onPrimaryContainer = PolishedOnPrimaryContainerDark,
    secondary = PolishedSecondaryDark,
    onSecondary = PolishedOnSecondaryDark,
    secondaryContainer = PolishedSecondaryContainerDark,
    onSecondaryContainer = PolishedOnSecondaryContainerDark,
    tertiary = PolishedTertiaryDark,
    onTertiary = PolishedOnTertiaryDark,
    tertiaryContainer = PolishedTertiaryContainerDark,
    onTertiaryContainer = PolishedOnTertiaryContainerDark,
    background = PolishedBgDark,
    onBackground = PolishedTextDark,
    surface = PolishedSurfaceDark,
    onSurface = PolishedOnSurfaceDark,
    surfaceVariant = PolishedSurfaceVariantDark,
    onSurfaceVariant = PolishedOnSurfaceVariantDark,
    outline = PolishedOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PolishedPrimaryLight,
    onPrimary = PolishedOnPrimaryLight,
    primaryContainer = PolishedPrimaryContainerLight,
    onPrimaryContainer = PolishedOnPrimaryContainerLight,
    secondary = PolishedSecondaryLight,
    onSecondary = PolishedOnSecondaryLight,
    secondaryContainer = PolishedSecondaryContainerLight,
    onSecondaryContainer = PolishedOnSecondaryContainerLight,
    tertiary = PolishedTertiaryLight,
    onTertiary = PolishedOnTertiaryLight,
    tertiaryContainer = PolishedTertiaryContainerLight,
    onTertiaryContainer = PolishedOnTertiaryContainerLight,
    background = PolishedBgLight,
    onBackground = PolishedTextLight,
    surface = PolishedSurfaceLight,
    onSurface = PolishedOnSurfaceLight,
    surfaceVariant = PolishedSurfaceVariantLight,
    onSurfaceVariant = PolishedOnSurfaceVariantLight,
    outline = PolishedOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamicColor false by default to showcase our beautiful Emerald Slate palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
