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

private val LightColorScheme = lightColorScheme(
    primary = MedicalTealPrimary,
    onPrimary = MedicalTealOnPrimary,
    primaryContainer = MedicalTealPrimaryContainer,
    onPrimaryContainer = MedicalTealOnPrimaryContainer,
    secondary = ClinicalBlueSecondary,
    onSecondary = ClinicalBlueOnSecondary,
    secondaryContainer = ClinicalBlueSecondaryContainer,
    onSecondaryContainer = ClinicalBlueOnSecondaryContainer,
    tertiary = AccentAmberTertiary,
    onTertiary = AccentAmberOnTertiary,
    tertiaryContainer = AccentAmberTertiaryContainer,
    onTertiaryContainer = AccentAmberOnTertiaryContainer,
    error = MedicalRedError,
    onError = MedicalRedOnError,
    errorContainer = MedicalRedErrorContainer,
    onErrorContainer = MedicalRedOnErrorContainer,
    background = MedicalBgLight,
    onBackground = Color(0xFF0F172A),
    surface = MedicalSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = MedicalSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = MedicalOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005048),
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF003549),
    secondaryContainer = Color(0xFF004D68),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF643F00),
    onTertiaryContainer = Color(0xFFFDE68A),
    error = Color(0xFFF87171),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0B111A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131D2A),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature medical branding palette
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
