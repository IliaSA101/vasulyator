package io.github.iliasa101.vasulyatr.domain

/** Нажатия клавиш — единственный способ изменить состояние калькулятора. */
sealed interface CalculatorKey {
    data class Digit(val value: Int) : CalculatorKey {
        init {
            require(value in 0..9) { "Digit must be 0..9, was $value" }
        }
    }

    data class Operator(val operation: Operation) : CalculatorKey

    data object Equals : CalculatorKey

    data object Backspace : CalculatorKey

    data object Clear : CalculatorKey
}

/**
 * Выражение вида «left operation right». Хранятся только введённые данные,
 * результат вычисляется из них — так состояние легко сохранить и восстановить.
 */
data class CalculatorState(
    val left: String = "",
    val operation: Operation? = null,
    val right: String = "",
    val isEvaluated: Boolean = false,
) {
    val result: CalculationResult?
        get() = if (isEvaluated && operation != null && right.isNotEmpty()) {
            Arithmetic.calculate(left.toLong(), operation, right.toLong())
        } else {
            null
        }
}

object CalculatorReducer {

    const val MAX_DIGITS = 9

    fun reduce(state: CalculatorState, key: CalculatorKey): CalculatorState = when (key) {
        is CalculatorKey.Digit -> state.inputDigit(key.value)
        is CalculatorKey.Operator -> state.inputOperation(key.operation)
        CalculatorKey.Equals -> state.evaluate()
        CalculatorKey.Backspace -> state.erase()
        CalculatorKey.Clear -> CalculatorState()
    }

    private fun CalculatorState.inputDigit(digit: Int): CalculatorState = when {
        isEvaluated -> CalculatorState(left = digit.toString())
        operation == null -> copy(left = left.appendDigit(digit))
        else -> copy(right = right.appendDigit(digit))
    }

    private fun CalculatorState.inputOperation(operation: Operation): CalculatorState = when {
        // После «=» продолжаем считать с результатом; для деления — с неполным частным.
        isEvaluated -> when (val result = result) {
            is CalculationResult.Value -> CalculatorState(result.value.toString(), operation)
            is CalculationResult.QuotientWithRemainder -> CalculatorState(result.quotient.toString(), operation)
            else -> CalculatorState()
        }
        // «5 + 3 ×» — сначала досчитываем 5 + 3, потом продолжаем.
        right.isNotEmpty() -> evaluate().inputOperation(operation)
        else -> copy(left = left.ifEmpty { "0" }, operation = operation)
    }

    private fun CalculatorState.evaluate(): CalculatorState =
        if (operation != null && right.isNotEmpty()) copy(isEvaluated = true) else this

    private fun CalculatorState.erase(): CalculatorState = when {
        isEvaluated -> copy(isEvaluated = false)
        right.isNotEmpty() -> copy(right = right.dropLast(1))
        operation != null -> copy(operation = null)
        // Не оставляем одинокий минус от отрицательного результата.
        else -> copy(left = left.dropLast(1).takeUnless { it == "-" }.orEmpty())
    }

    private fun String.appendDigit(digit: Int): String = when {
        this == "0" -> digit.toString()
        length >= MAX_DIGITS -> this
        else -> this + digit
    }
}
