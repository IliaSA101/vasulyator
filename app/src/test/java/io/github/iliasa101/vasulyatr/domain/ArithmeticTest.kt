package io.github.iliasa101.vasulyatr.domain

import io.github.iliasa101.vasulyatr.domain.CalculationResult.DivisionByZero
import io.github.iliasa101.vasulyatr.domain.CalculationResult.Overflow
import io.github.iliasa101.vasulyatr.domain.CalculationResult.QuotientWithRemainder
import io.github.iliasa101.vasulyatr.domain.CalculationResult.Value
import org.junit.Assert.assertEquals
import org.junit.Test

class ArithmeticTest {

    @Test
    fun `division gives quotient and remainder`() {
        assertEquals(QuotientWithRemainder(3, 1), Arithmetic.calculate(10, Operation.DIVIDE, 3))
        assertEquals(QuotientWithRemainder(3, 0), Arithmetic.calculate(12, Operation.DIVIDE, 4))
        assertEquals(QuotientWithRemainder(0, 3), Arithmetic.calculate(3, Operation.DIVIDE, 10))
    }

    @Test
    fun `remainder of negative dividend is not negative`() {
        assertEquals(QuotientWithRemainder(-3, 2), Arithmetic.calculate(-7, Operation.DIVIDE, 3))
    }

    @Test
    fun `division by zero is reported`() {
        assertEquals(DivisionByZero, Arithmetic.calculate(5, Operation.DIVIDE, 0))
    }

    @Test
    fun `other operations`() {
        assertEquals(Value(12), Arithmetic.calculate(7, Operation.ADD, 5))
        assertEquals(Value(-2), Arithmetic.calculate(3, Operation.SUBTRACT, 5))
        assertEquals(Value(42), Arithmetic.calculate(6, Operation.MULTIPLY, 7))
    }

    @Test
    fun `overflow is reported instead of wrapping around`() {
        assertEquals(Overflow, Arithmetic.calculate(Long.MAX_VALUE, Operation.ADD, 1))
        assertEquals(Overflow, Arithmetic.calculate(999_999_999_999_999_999, Operation.MULTIPLY, 999_999_999))
    }
}
