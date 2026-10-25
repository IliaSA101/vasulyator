package io.github.iliasa101.vasulyatr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Фирменные цвета — те же, что у иконки: индиго и оранжевый «остаток».
// Dynamic color (Material You) не включаем намеренно, чтобы приложение выглядело одинаково на любом телефоне.
private val Indigo = Color(0xFF283593)
private val IndigoLight = Color(0xFF9FA8DA)
private val Orange = Color(0xFFFF8F00)
private val OrangeLight = Color(0xFFFFB74D)

private val LightColors = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E3F7),
    onPrimaryContainer = Color(0xFF0F1A5C),
    tertiary = Orange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFF5C3300),
    background = Color(0xFFF4F5FA),
    onBackground = Color(0xFF1B1B21),
    surface = Color.White,
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = Color(0xFFE9EAF2),
    onSurfaceVariant = Color(0xFF5A5D72),
)

private val DarkColors = darkColorScheme(
    primary = IndigoLight,
    onPrimary = Color(0xFF0F1A5C),
    primaryContainer = Color(0xFF2E3567),
    onPrimaryContainer = Color(0xFFE0E3F7),
    tertiary = OrangeLight,
    onTertiary = Color(0xFF3D2200),
    tertiaryContainer = Color(0xFF5C3A10),
    onTertiaryContainer = Color(0xFFFFE0B2),
    background = Color(0xFF121318),
    onBackground = Color(0xFFE4E4EC),
    surface = Color(0xFF1C1D24),
    onSurface = Color(0xFFE4E4EC),
    surfaceVariant = Color(0xFF2A2C36),
    onSurfaceVariant = Color(0xFFB4B6C8),
)

@Composable
fun VasulyatrTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
