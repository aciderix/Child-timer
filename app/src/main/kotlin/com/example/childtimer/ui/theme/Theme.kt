package com.example.childtimer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCoralDark,
    onPrimary = OnSurfaceDark,
    primaryContainer = PrimaryCoral,
    onPrimaryContainer = OnPrimaryCoral,
    secondary = SecondaryCyanDark,
    onSecondary = OnSurfaceDark,
    secondaryContainer = SecondaryCyan,
    tertiary = TertiarySunnyDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCoral,
    onPrimary = OnPrimaryCoral,
    primaryContainer = PrimaryContainerCoral,
    onPrimaryContainer = OnSurfaceWarm,
    secondary = SecondaryCyan,
    onSecondary = OnSecondaryCyan,
    secondaryContainer = SecondaryContainerCyan,
    tertiary = TertiarySunny,
    tertiaryContainer = TertiaryContainerSunny,
    background = BackgroundWarm,
    surface = SurfaceWarm,
    surfaceVariant = SurfaceVariantWarm,
    onBackground = OnSurfaceWarm,
    onSurface = OnSurfaceWarm,
    outline = OutlineWarm
)

@Composable
fun ChildTimerTheme(
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
        typography = Typography,
        content = content
    )
}
