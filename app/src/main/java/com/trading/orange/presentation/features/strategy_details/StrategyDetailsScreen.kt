package com.trading.orange.presentation.features.strategy_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.R
import com.trading.orange.domain.model.StrategyArticle
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.modifiers.toHtmlString
import com.trading.orange.presentation.common.theme.ColorLightGray
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

@Composable
fun StrategyDetailsScreenRoot(
    strategyId: Int,
    navController: NavController,
    viewModel: StrategyDetailsViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = strategyId) {
        viewModel.setId(strategyId)
    }
    StrategyDetailsScreen(
        state = state,
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun StrategyDetailsScreen(
    state: StrategyDetailsScreenState,
    navigateUp: () -> Unit
) {
    MainScreensLayout(
        paddingTop = 16.dp,
        paddingBottom = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .safeSingleClick {
                        navigateUp()
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {

                if (state.strategyArticle != null) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = LightBlue,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = state.strategyArticle.title,
                            style = DefaultTextStyle.copy(
                                color = Color.White,
                                fontFamily = FontFamilyAvenirRegular,
                                fontSize = 16.sp
                            )
                        )
                        Row(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.type_of_strategy),
                                style = DefaultTextStyle.copy(
                                    color = ColorLightGray,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = state.strategyArticle.type,
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.timeframe),
                                style = DefaultTextStyle.copy(
                                    color = ColorLightGray,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = state.strategyArticle.timeframe,
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.assets_to_trade),
                                style = DefaultTextStyle.copy(
                                    color = ColorLightGray,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = state.strategyArticle.assets,
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.CenterVertically)
                                    .background(
                                        color = state.strategyArticle.getDifficultyColor(),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = state.strategyArticle.difficultyTitle,
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontFamily = FontFamilyAvenirRegular,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.strategyArticle.text.toHtmlString(),
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirRegular,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StrategyDetailsScreenPreview() {
    TradingOrangeTheme {
        StrategyDetailsScreen(
            state = StrategyDetailsScreenState(
                strategyArticle = StrategyArticle(
                    id = 1,
                    title = "Bollinge R’S friend (BB, MACD)",
                    type = "Trend",
                    timeframe = "5 - 15 s",
                    assets = "Currency pairs",
                    difficultyTitle = "Easy",
                    difficultyColorHex = "#37C757",
                    text = "Bollinger Bands (BB) is a world-famous momentum indicator created by financial analyst John Bollinger. If you are familiar with BB, you know how good it is in indicating trends and flats. This strategy lets you improve your trading experience by inviting Bollinger’s “best friend” — MACD.",
                    imageDataProvider = null
                )
            ),
            navigateUp = {}
        )
    }
}