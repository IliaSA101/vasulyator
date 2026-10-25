package io.github.iliasa101.vasulyatr.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.github.iliasa101.vasulyatr.domain.CalculatorKey
import io.github.iliasa101.vasulyatr.domain.CalculatorReducer
import io.github.iliasa101.vasulyatr.domain.CalculatorState
import io.github.iliasa101.vasulyatr.domain.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Держит состояние и переживает поворот экрана и смерть процесса через [SavedStateHandle]. */
class CalculatorViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    private val _state = MutableStateFlow(savedState.restore())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onKey(key: CalculatorKey) {
        _state.update { CalculatorReducer.reduce(it, key) }
        savedState.save(_state.value)
    }

    private companion object {
        const val LEFT = "left"
        const val OPERATION = "operation"
        const val RIGHT = "right"
        const val EVALUATED = "evaluated"

        fun SavedStateHandle.save(state: CalculatorState) {
            set(LEFT, state.left)
            set(OPERATION, state.operation?.name)
            set(RIGHT, state.right)
            set(EVALUATED, state.isEvaluated)
        }

        fun SavedStateHandle.restore() = CalculatorState(
            left = get<String>(LEFT).orEmpty(),
            operation = get<String>(OPERATION)?.let { name -> Operation.entries.find { it.name == name } },
            right = get<String>(RIGHT).orEmpty(),
            isEvaluated = get<Boolean>(EVALUATED) ?: false,
        )
    }
}
