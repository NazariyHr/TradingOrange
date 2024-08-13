package com.trading.orange.presentation.features.signals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.model.rates.SignalType
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.signals.component.SignalItem
import java.util.Calendar
import kotlin.random.Random

@Composable
fun SignalsScreenRoot(
    navController: NavController,
    viewModel: SignalsViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SignalsScreen(
        state = state,
        onCopySignalClicked = viewModel::onCopySignalClicked
    )
}

@Composable
private fun SignalsScreen(
    state: SignalsScreenState,
    onCopySignalClicked: (Signal) -> Unit
) {
    MainScreensLayout(
        paddingTop = 16.dp
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = "Signals",
                style = DefaultTextStyle.copy(
                    color = Color.White,
                    fontFamily = FontFamilyAvenirHeavy,
                    fontSize = 20.sp
                )
            )
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = state.signals,
                    key = { signal -> signal.id }
                ) { signal ->
                    SignalItem(
                        signal = signal,
                        onCopySignalClicked = {
                            onCopySignalClicked(signal)
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SignalsScreenPreview() {
    val signals = mutableListOf<Signal>()
    repeat(4) {
        val startTime = Calendar.getInstance().timeInMillis - 1000 * 60 * (signals.count() + 1)
        val timeSeconds = 60 * (signals.count() + 1) + 40
        signals.add(
            Signal(
                id = signals.count(),
                instrument = "GBP".toInstrument(),
                currInstrumentRate = 45f,
                amountForBet = 100,
                copies = Random.nextInt(10, 70),
                type = SignalType.entries[Random.nextInt(0, 4)],
                startTime = startTime,
                timeSeconds = timeSeconds,
                availableToBet = signals.count() < 2
            )
        )
    }
    TradingOrangeTheme {
        SignalsScreen(
            state = SignalsScreenState(
                signals = signals
            ),
            onCopySignalClicked = {}
        )
    }
}