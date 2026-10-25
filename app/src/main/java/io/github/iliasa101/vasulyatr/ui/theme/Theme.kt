package io.github.iliasa101.vasulyatr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Яркая детская палитра. Тёмной темы нет намеренно: важнее крупный контраст и узнаваемые цвета. */
object KidColors {
    val Primary = Color(0xFF1565C0)
    val Background = Color(0xFFFFFDE7)
    val Success = Color(0xFF2E7D32)
    val DigitKey = Color(0xFFE3F2FD)
    val OperatorKey = Color(0xFFFFE0B2)
    val ClearKey = Color(0xFFFFCDD2)
}

private val KidColorScheme = lightColorScheme(
    primary = KidColors.Primary,
    background = KidColors.Background,
    surface = KidColors.Background,
)

@Composable
fun VasulyatrTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = KidColorScheme, content = content)
}
