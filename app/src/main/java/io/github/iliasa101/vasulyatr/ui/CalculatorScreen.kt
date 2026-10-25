package io.github.iliasa101.vasulyatr.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.iliasa101.vasulyatr.R
import io.github.iliasa101.vasulyatr.domain.CalculationResult
import io.github.iliasa101.vasulyatr.domain.CalculatorKey
import io.github.iliasa101.vasulyatr.domain.CalculatorState
import io.github.iliasa101.vasulyatr.domain.Operation
import io.github.iliasa101.vasulyatr.ui.theme.VasulyatrTheme

// Моноширинные цифры: числа не «прыгают» по ширине при наборе.
private val Digits = TextStyle(fontFeatureSettings = "tnum")

@Composable
fun CalculatorScreen(state: CalculatorState, onKey: (CalculatorKey) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Display(
            state,
            Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        Keypad(onKey)
    }
}

@Composable
private fun Display(state: CalculatorState, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        val result = state.result
        Text(
            state.expression(),
            style = Digits,
            fontSize = if (result == null) 48.sp else 30.sp,
            color = if (result == null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.StartEllipsis,
        )
        if (result != null) ResultBlock(state, result)
    }
}

@Composable
private fun ResultBlock(state: CalculatorState, result: CalculationResult) {
    val isError = result is CalculationResult.DivisionByZero || result is CalculationResult.Overflow
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                mainText(result),
                style = Digits,
                fontSize = if (isError) 26.sp else 56.sp,
                fontWeight = FontWeight.Bold,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
            )
            if (result is CalculationResult.QuotientWithRemainder && result.remainder != 0L) {
                RemainderChip(result.remainder)
            }
        }
        if (result is CalculationResult.QuotientWithRemainder) {
            Text(
                checkText(state, result),
                style = Digits,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Остаток — главное в Васюлятре, поэтому он выделен фирменным оранжевым. */
@Composable
private fun RemainderChip(remainder: Long) {
    Text(
        stringResource(R.string.remainder, remainder),
        style = Digits,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

private val Operation.symbol: String
    get() = when (this) {
        Operation.ADD -> "+"
        Operation.SUBTRACT -> "−"
        Operation.MULTIPLY -> "×"
        Operation.DIVIDE -> ":"
    }

private fun CalculatorState.expression(): String = buildString {
    append(left.ifEmpty { "0" })
    operation?.let { append(" ${it.symbol} ").append(right) }
}

@Composable
private fun mainText(result: CalculationResult): String = when (result) {
    is CalculationResult.Value -> stringResource(R.string.result_value, result.value)
    is CalculationResult.QuotientWithRemainder -> stringResource(R.string.result_value, result.quotient)
    CalculationResult.DivisionByZero -> stringResource(R.string.error_division_by_zero)
    CalculationResult.Overflow -> stringResource(R.string.error_overflow)
}

/** Подсказка, по которой можно проверить себя: 3 × 3 + 1 = 10. */
@Composable
private fun checkText(state: CalculatorState, result: CalculationResult.QuotientWithRemainder): String =
    if (result.remainder == 0L) {
        stringResource(R.string.check_exact, result.quotient, state.right, state.left)
    } else {
        stringResource(R.string.check_with_remainder, result.quotient, state.right, result.remainder, state.left)
    }

private enum class KeyStyle { DIGIT, OPERATOR, CLEAR, EQUALS }

private data class KeySpec(
    val label: String,
    val key: CalculatorKey,
    val style: KeyStyle,
    @param:StringRes val description: Int? = null,
)

private fun digit(value: Int) = KeySpec(value.toString(), CalculatorKey.Digit(value), KeyStyle.DIGIT)

private fun operator(operation: Operation, @StringRes description: Int) =
    KeySpec(operation.symbol, CalculatorKey.Operator(operation), KeyStyle.OPERATOR, description)

private val KeypadRows = listOf(
    listOf(
        KeySpec("C", CalculatorKey.Clear, KeyStyle.CLEAR, R.string.key_clear),
        KeySpec("⌫", CalculatorKey.Backspace, KeyStyle.CLEAR, R.string.key_backspace),
        operator(Operation.DIVIDE, R.string.key_divide),
        operator(Operation.MULTIPLY, R.string.key_multiply),
    ),
    listOf(digit(7), digit(8), digit(9), operator(Operation.SUBTRACT, R.string.key_subtract)),
    listOf(digit(4), digit(5), digit(6), operator(Operation.ADD, R.string.key_add)),
)

private val BottomRows = listOf(
    listOf(digit(1), digit(2), digit(3)),
    listOf(digit(0)),
)

private val EqualsKey = KeySpec("=", CalculatorKey.Equals, KeyStyle.EQUALS, R.string.key_equals)

private val KeyGap = 10.dp

/**
 * Раскладка как у привычного калькулятора: «:» сверху, высокая «=» справа внизу.
 * «0» занимает три колонки, чтобы в него было легко попасть.
 */
@Composable
private fun Keypad(onKey: (CalculatorKey) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(KeyGap)) {
        KeypadRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(KeyGap)) {
                row.forEach { spec -> KeyButton(spec, onKey, Modifier.weight(1f).aspectRatio(1.15f)) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(KeyGap)) {
            Column(Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(KeyGap)) {
                BottomRows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(KeyGap)) {
                        row.forEach { spec ->
                            KeyButton(spec, onKey, Modifier.weight(1f).height(KeyHeight))
                        }
                    }
                }
            }
            Box(Modifier.weight(1f)) {
                KeyButton(EqualsKey, onKey, Modifier.fillMaxWidth().height(KeyHeight * 2 + KeyGap))
            }
        }
    }
}

// Высота нижних клавиш согласована с квадратными верхними на типичной ширине телефона.
private val KeyHeight = 72.dp

@Composable
private fun KeyButton(spec: KeySpec, onKey: (CalculatorKey) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (spec.style) {
        KeyStyle.DIGIT -> colors.surface to colors.onSurface
        KeyStyle.OPERATOR -> colors.primaryContainer to colors.onPrimaryContainer
        KeyStyle.CLEAR -> colors.surfaceVariant to colors.error
        KeyStyle.EQUALS -> colors.tertiary to colors.onTertiary
    }
    val description = spec.description?.let { stringResource(it) }
    Button(
        onClick = { onKey(spec.key) },
        modifier = modifier.semantics { if (description != null) contentDescription = description },
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(spec.label, fontSize = 30.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(name = "Светлая", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun CalculatorScreenLightPreview() {
    VasulyatrTheme(darkTheme = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            CalculatorScreen(
                state = CalculatorState(left = "10", operation = Operation.DIVIDE, right = "3", isEvaluated = true),
                onKey = {},
            )
        }
    }
}

@Preview(name = "Тёмная", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun CalculatorScreenDarkPreview() {
    VasulyatrTheme(darkTheme = true) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            CalculatorScreen(state = CalculatorState(left = "125", operation = Operation.MULTIPLY), onKey = {})
        }
    }
}
