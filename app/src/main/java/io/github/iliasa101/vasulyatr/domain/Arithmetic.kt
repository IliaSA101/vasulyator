package io.github.iliasa101.vasulyatr.domain

enum class Operation { ADD, SUBTRACT, MULTIPLY, DIVIDE }

sealed interface CalculationResult {
    data class Value(val value: Long) : CalculationResult

    /** Деление с остатком: dividend = quotient × divisor + remainder, 0 ≤ remainder < divisor. */
    data class QuotientWithRemainder(val quotient: Long, val remainder: Long) : CalculationResult

    data object DivisionByZero : CalculationResult

    data object Overflow : CalculationResult
}

/** Целочисленная арифметика «как в школе». Без побочных эффектов, без Android-зависимостей. */
object Arithmetic {

    fun calculate(left: Long, operation: Operation, right: Long): CalculationResult = try {
        when (operation) {
            Operation.ADD -> CalculationResult.Value(Math.addExact(left, right))
            Operation.SUBTRACT -> CalculationResult.Value(Math.subtractExact(left, right))
            Operation.MULTIPLY -> CalculationResult.Value(Math.multiplyExact(left, right))
            Operation.DIVIDE -> divide(left, right)
        }
    } catch (_: ArithmeticException) {
        CalculationResult.Overflow
    }

    // Делитель вводится только цифрами, поэтому он всегда ≥ 0. floorDiv/floorMod дают
    // неотрицательный остаток и для отрицательного делимого: −7 : 3 = −3, остаток 2.
    private fun divide(dividend: Long, divisor: Long): CalculationResult =
        if (divisor == 0L) {
            CalculationResult.DivisionByZero
        } else {
            CalculationResult.QuotientWithRemainder(
                quotient = Math.floorDiv(dividend, divisor),
                remainder = Math.floorMod(dividend, divisor),
            )
        }
}
