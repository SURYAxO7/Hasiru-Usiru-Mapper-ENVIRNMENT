package com.hasiru.usiru.mapper.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen,
    onPrimary = OnPrimaryLight,
    primaryContainer = SageGreen,
    secondary = ForestGreen,
    tertiary = OxygenGold,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = PitRed
)

private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    onPrimary = OnPrimaryDark,
    primaryContainer = ForestGreen,
    secondary = SageGreen,
    tertiary = OxygenGold,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = PitRed
)

@Composable
fun HasiruUsiruTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
        typography = HasiruTypography,
        content = content
    )
}
