package de.goldoust.eschatontools.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val TacticalDarkColorScheme = darkColorScheme(
    primary = TacticalAmberPrimary,
    onPrimary = TacticalAmberOnPrimary,
    primaryContainer = TacticalAmberContainer,
    onPrimaryContainer = TacticalAmberOnContainer,
    secondary = TacticalGreenSecondary,
    onSecondary = TacticalGreenOnSecondary,
    secondaryContainer = TacticalGreenContainer,
    onSecondaryContainer = TacticalGreenOnContainer,
    tertiary = EmergencyRedTertiary,
    onTertiary = EmergencyRedOnTertiary,
    tertiaryContainer = EmergencyRedContainer,
    onTertiaryContainer = EmergencyRedOnContainer,
    background = SurvivalBackground,
    onBackground = SurvivalTextPrimary,
    surface = SurvivalSurface,
    onSurface = SurvivalTextPrimary,
    surfaceVariant = SurvivalSurfaceVariant,
    onSurfaceVariant = SurvivalTextSecondary,
    error = EmergencyRedTertiary,
    onError = EmergencyRedOnTertiary
)

@Composable
fun EschatonToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to false for consistent tactical high-contrast UI
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> TacticalDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}