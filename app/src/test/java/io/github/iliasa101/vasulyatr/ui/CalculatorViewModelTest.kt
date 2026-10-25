package io.github.iliasa101.vasulyatr.ui

import androidx.lifecycle.SavedStateHandle
import io.github.iliasa101.vasulyatr.domain.CalculationResult
import io.github.iliasa101.vasulyatr.domain.CalculatorKey
import io.github.iliasa101.vasulyatr.domain.Operation
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorViewModelTest {

    @Test
    fun `state survives process death via SavedStateHandle`() {
        val handle = SavedStateHandle()
        CalculatorViewModel(handle).apply {
            onKey(CalculatorKey.Digit(1))
            onKey(CalculatorKey.Digit(0))
            onKey(CalculatorKey.Operator(Operation.DIVIDE))
            onKey(CalculatorKey.Digit(3))
            onKey(CalculatorKey.Equals)
        }

        val restored = CalculatorViewModel(handle).state.value

        assertEquals(CalculationResult.QuotientWithRemainder(3, 1), restored.result)
    }
}
