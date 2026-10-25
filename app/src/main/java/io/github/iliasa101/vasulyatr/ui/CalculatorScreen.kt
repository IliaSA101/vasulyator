package io.github.iliasa101.vasulyatr.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.iliasa101.vasulyatr.R
import io.github.iliasa101.vasulyatr.domain.CalculationResult
import io.github.iliasa101.vasulyatr.domain.CalculatorKey
import io.github.iliasa101.vasulyatr.domain.CalculatorState
import io.github.iliasa101.vasulyatr.domain.Operation
import io.github.iliasa101.vasulyatr.ui.theme.KidColors
import io.github.iliasa101.vasulyatr.ui.theme.VasulyatrTheme

@Composable
fun CalculatorScreen(state: CalculatorState, onKey: (CalculatorKey) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
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
    Card(modifier, shape = RoundedCornerShape(24.dp)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
        ) {
            Text(state.expression(), fontSize = 40.sp, textAlign = TextAlign.End)
            val result = state.result ?: return@Column
            val isError = result is CalculationResult.DivisionByZero || result is CalculationResult.Overflow
            Text(
                resultText(result),
                fontSize = if (isError) 28.sp else 44.sp,
                fontWeight = FontWeight.Bold,
                color = if (isError) MaterialTheme.colorScheme.error else KidColors.Success,
                textAlign = TextAlign.End,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            if (result is CalculationResult.QuotientWithRemainder) {
                Text(
                    checkText(state, result),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
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
private fun resultText(result: CalculationResult): String = when (result) {
    is CalculationResult.Value -> stringResource(R.string.result_value, result.value)
    is CalculationResult.QuotientWithRemainder ->
        if (result.remainder == 0L) {
            stringResource(R.string.result_value, result.quotient)
        } else {
            stringResource(R.string.result_with_remainder, result.quotient, result.remainder)
        }
    CalculationResult.DivisionByZero -> stringResource(R.string.error_division_by_zero)
    CalculationResult.Overflow -> stringResource(R.string.error_overflow)
}

/** Подсказка, по которой ребёнок может проверить себя: 3 × 3 + 1 = 10. */
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
    listOf(digit(7), digit(8), digit(9), operator(Operation.DIVIDE, R.string.key_divide)),
    listOf(digit(4), digit(5), digit(6), operator(Operation.MULTIPLY, R.string.key_multiply)),
    listOf(digit(1), digit(2), digit(3), operator(Operation.SUBTRACT, R.string.key_subtract)),
    listOf(
        KeySpec("C", CalculatorKey.Clear, KeyStyle.CLEAR, R.string.key_clear),
        digit(0),
        KeySpec("⌫", CalculatorKey.Backspace, KeyStyle.OPERATOR, R.string.key_backspace),
        operator(Operation.ADD, R.string.key_add),
    ),
)

private val EqualsKey = KeySpec("=", CalculatorKey.Equals, KeyStyle.EQUALS, R.string.key_equals)

@Composable
private fun Keypad(onKey: (CalculatorKey) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        KeypadRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { spec -> KeyButton(spec, onKey, Modifier.weight(1f)) }
            }
        }
        KeyButton(EqualsKey, onKey, Modifier.fillMaxWidth())
    }
}

@Composable
private fun KeyButton(spec: KeySpec, onKey: (CalculatorKey) -> Unit, modifier: Modifier = Modifier) {
    val (container, content) = when (spec.style) {
        KeyStyle.DIGIT -> KidColors.DigitKey to Color.Black
        KeyStyle.OPERATOR -> KidColors.OperatorKey to Color.Black
        KeyStyle.CLEAR -> KidColors.ClearKey to Color.Black
        KeyStyle.EQUALS -> KidColors.Success to Color.White
    }
    val description = spec.description?.let { stringResource(it) }
    Button(
        onClick = { onKey(spec.key) },
        modifier = modifier.semantics { if (description != null) contentDescription = description },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
    ) {
        Text(spec.label, fontSize = 32.sp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Preview(showBackground = true, heightDp = 780)
@Composable
private fun CalculatorScreenPreview() {
    VasulyatrTheme {
        CalculatorScreen(
            state = CalculatorState(left = "10", operation = Operation.DIVIDE, right = "3", isEvaluated = true),
            onKey = {},
        )
    }
}
