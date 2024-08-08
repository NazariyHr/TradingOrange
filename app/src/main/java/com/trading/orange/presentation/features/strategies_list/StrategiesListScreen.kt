package com.trading.orange.presentation.features.strategies_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.navigation.Screen

@Composable
fun StrategiesListScreenRoot(
    navController: NavController,
    viewModel: StrategiesListViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StrategiesListScreen(
        state = state,
        onStrategyClicked = {
            navController.navigate(Screen.StrategyDetails(1))
        }
    )
}

@Composable
private fun StrategiesListScreen(
    state: StrategiesListScreenState,
    onStrategyClicked: () -> Unit
) {
    MainScreensLayout {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = state.title,
                style = DefaultTextStyle.copy(
                    color = Color.White,
                    fontFamily = FontFamilyAvenirHeavy,
                    fontSize = 20.sp
                )
            )

            Button(
                onClick = {
                    onStrategyClicked()
                }
            ) {
                Text(
                    text = "Strategy details",
                    style = DefaultTextStyle.copy(
                        color = Color.White,
                        fontFamily = FontFamilyAvenirHeavy,
                        fontSize = 20.sp
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun StrategiesListScreenPreview() {
    TradingOrangeTheme {
        StrategiesListScreen(
            state = StrategiesListScreenState(),
            onStrategyClicked = {}
        )
    }
}