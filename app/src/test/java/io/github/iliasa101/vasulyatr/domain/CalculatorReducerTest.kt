package io.github.iliasa101.vasulyatr.domain

import io.github.iliasa101.vasulyatr.domain.CalculationResult.QuotientWithRemainder
import io.github.iliasa101.vasulyatr.domain.CalculationResult.Value
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorReducerTest {

    /** Мини-язык нажатий: цифры, + - * :, = (равно), < (стереть), C (сброс). */
    private fun press(keys: String, from: CalculatorState = CalculatorState()): CalculatorState =
        keys.fold(from) { state, char -> CalculatorReducer.reduce(state, char.toKey()) }

    private fun Char.toKey(): CalculatorKey = when (this) {
        '+' -> CalculatorKey.Operator(Operation.ADD)
        '-' -> CalculatorKey.Operator(Operation.SUBTRACT)
        '*' -> CalculatorKey.Operator(Operation.MULTIPLY)
        ':' -> CalculatorKey.Operator(Operation.DIVIDE)
        '=' -> CalculatorKey.Equals
        '<' -> CalculatorKey.Backspace
        'C' -> CalculatorKey.Clear
        else -> CalculatorKey.Digit(digitToInt())
    }

    @Test
    fun `ten divided by three`() {
        assertEquals(QuotientWithRemainder(3, 1), press("10:3=").result)
    }

    @Test
    fun `result appears only after equals`() {
        assertNull(press("10:3").result)
    }

    @Test
    fun `chaining continues from quotient and evaluates pending operation`() {
        assertEquals(Value(5), press("10:3=+2=").result)
        assertEquals(Value(16), press("5+3*2=").result)
    }

    @Test
    fun `digit after result starts a new expression`() {
        assertEquals(CalculatorState(left = "7"), press("2+2=7"))
    }

    @Test
    fun `input is limited and leading zeros are dropped`() {
        assertEquals("7", press("007").left)
        assertEquals("123456789", press("1234567890").left)
    }

    @Test
    fun `backspace steps back through the expression`() {
        assertEquals(CalculatorState("12", Operation.DIVIDE, "3"), press("12:3=<"))
        assertEquals(CalculatorState("12", Operation.DIVIDE), press("12:3<"))
        assertEquals(CalculatorState("12"), press("12:<"))
        // Стираем 7 из «−7» — одинокий минус не остаётся.
        assertEquals(CalculatorState(), press("<", from = CalculatorState(left = "-7")))
    }

    @Test
    fun `operator without left operand starts from zero`() {
        assertEquals(Value(-3), press("-3=").result)
    }

    @Test
    fun `clear resets everything`() {
        assertEquals(CalculatorState(), press("12:3=C"))
    }
}
