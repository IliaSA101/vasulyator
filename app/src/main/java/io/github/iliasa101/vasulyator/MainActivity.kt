package io.github.iliasa101.vasulyator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.iliasa101.vasulyator.ui.CalculatorScreen
import io.github.iliasa101.vasulyator.ui.CalculatorViewModel
import io.github.iliasa101.vasulyator.ui.theme.VasulyatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VasulyatorTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: CalculatorViewModel = viewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    CalculatorScreen(state, viewModel::onKey)
                }
            }
        }
    }
}
