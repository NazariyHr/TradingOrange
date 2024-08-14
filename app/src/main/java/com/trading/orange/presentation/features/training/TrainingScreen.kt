package com.trading.orange.presentation.features.training

import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.R
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.BetType
import com.trading.orange.domain.model.rates.CandleStick
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.ColorGrayLighter
import com.trading.orange.presentation.common.theme.ColorLightGray
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.training.components.AssetItem
import com.trading.orange.presentation.features.training.components.BetResultItem
import com.trading.orange.presentation.features.training.components.BetTimerItem
import com.trading.orange.presentation.features.training.components.DownButton
import com.trading.orange.presentation.features.training.components.TradeHistoryItem
import com.trading.orange.presentation.features.training.components.UpButton
import com.trading.orange.presentation.features.training.components.chart.CandleChart
import com.trading.orange.presentation.features.training.components.chart.ChartDrawType
import com.trading.orange.presentation.features.training.components.chart.LinearChart
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@Composable
fun TrainingScreenRoot(
    navController: NavController,
    viewModel: TrainingViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TrainingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun TrainingScreen(
    state: TrainingScreenState,
    onAction: (TrainingScreenAction) -> Unit
) {
    val d = LocalDensity.current

    var minutes by rememberSaveable {
        mutableIntStateOf(0)
    }
    val minutesStr by remember(minutes) {
        derivedStateOf {
            if (minutes < 10) {
                "0$minutes"
            } else {
                "$minutes"
            }
        }
    }
    var minutesDropDownOpened by rememberSaveable {
        mutableStateOf(false)
    }

    var seconds by rememberSaveable {
        mutableIntStateOf(0)
    }
    val secondsStr by remember(minutes) {
        derivedStateOf {
            if (seconds < 10) {
                "0$seconds"
            } else {
                "$seconds"
            }
        }
    }

    var timeOptionsDividerHeight by remember {
        mutableStateOf(0.dp)
    }
    var timeAndAmoutHeight by remember {
        mutableStateOf(0.dp)
    }

    var amountDropDownOpened by rememberSaveable {
        mutableStateOf(false)
    }
    var amountOptions by remember {
        mutableStateOf(listOf(1, 5, 10, 20, 50, 100))
    }

    val payout by remember(state.preparedAmount, state.coefficient) {
        derivedStateOf {
            String.format(
                Locale.US,
                "$%.2f",
                (state.preparedAmount.toFloat() + state.preparedAmount.toFloat() * state.coefficient)
            )
        }
    }

    var chartVisibleDiapasonCount by remember {
        mutableStateOf(0)
    }

    MainScreensLayout(
        paddingStart = 0.dp,
        paddingEnd = 0.dp
    ) {
        Column {
            // Balance
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ic_refresh),
                        contentDescription = null,
                        modifier = Modifier
                            .border(
                                color = ColorGrayLighter,
                                width = 1.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(6.dp)
                            .align(Alignment.CenterVertically)
                            .safeSingleClick {
                                onAction(TrainingScreenAction.OnRefreshBalanceClicked)
                            }
                    )
                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = state.balanceFormatted,
                            style = DefaultTextStyle.copy(
                                color = ColorOrange,
                                fontSize = 20.sp,
                                fontFamily = FontFamilyAvenirHeavy
                            )
                        )
                        Text(
                            text = stringResource(id = R.string.practice_balance),
                            style = DefaultTextStyle.copy(
                                color = Color(101, 117, 144),
                                fontSize = 12.sp,
                                fontFamily = FontFamilyAvenirRegular
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.weight(1f)
            ) {
                //Main content with chart
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.showType == ShowType.CHART,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Currency drop down
                        Row(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(
                                        if (state.selectedInstrument == null) Modifier
                                        else Modifier
                                            .safeSingleClick {
                                                onAction(
                                                    TrainingScreenAction.OnShowTypeChange(
                                                        ShowType.ASSET_CHOOSER
                                                    )
                                                )
                                            }
                                            .background(
                                                color = LightBlue,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(12.dp)
                                    )
                            ) {
                                if (state.selectedInstrument != null) {
                                    Row(
                                        modifier = Modifier
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .height(24.dp)
                                                .width(36.dp)
                                                .align(Alignment.CenterVertically),
                                            painter = painterResource(id = state.selectedInstrument.getIcon()),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = "USD/" + state.selectedInstrument.name,
                                            style = DefaultTextStyle.copy(
                                                fontSize = 16.sp,
                                                fontFamily = FontFamilyAvenirRegular
                                            ),
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp)
                                                .weight(1f)
                                                .align(Alignment.CenterVertically)
                                        )

                                        Image(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(id = R.drawable.ic_drop_down),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }

                            // Change Chart draw type
                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .align(Alignment.CenterVertically)
                                    .background(
                                        color = LightBlue,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .safeSingleClick {
                                        val newDrawType = when (state.chartDrawType) {
                                            ChartDrawType.CANDLES -> ChartDrawType.LINEAR
                                            ChartDrawType.LINEAR -> ChartDrawType.CANDLES
                                        }
                                        onAction(
                                            TrainingScreenAction.OnChartDrawTypeChange(
                                                newDrawType
                                            )
                                        )
                                    }
                                    .padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_chart_type),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Show trade history button
                            if (state.betResults.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .align(Alignment.CenterVertically)
                                        .background(
                                            color = LightBlue,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .safeSingleClick {
                                            onAction(
                                                TrainingScreenAction.OnShowTypeChange(
                                                    ShowType.TRADE_HISTORY
                                                )
                                            )
                                        }
                                        .padding(12.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_history),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Chart, time and amount
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            // Chart
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = timeAndAmoutHeight)
                            ) {
                                when (state.chartDrawType) {
                                    ChartDrawType.CANDLES -> {
                                        CandleChart(
                                            modifier = Modifier.fillMaxHeight(),
                                            candles = state.candles,
                                            bet = state.bet,
                                            onNewVisibleDiapasonCount = { count ->
                                                chartVisibleDiapasonCount = count
                                            }
                                        )
                                    }

                                    ChartDrawType.LINEAR -> {
                                        LinearChart(
                                            modifier = Modifier.fillMaxHeight(),
                                            rates = state.rates,
                                            bet = state.bet,
                                            chartVisibleDiapasonCount = chartVisibleDiapasonCount
                                        )
                                    }
                                }

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = state.bet != null,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(
                                            start = 16.dp,
                                            bottom = 12.dp
                                        )
                                ) {
                                    state.bet?.let { bet ->
                                        BetTimerItem(
                                            startTime = bet.startTime,
                                            endTime = bet.startTime + bet.timeSeconds * 1000L
                                        )
                                    }
                                }

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = state.lastBetResult != null,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(
                                            end = 16.dp,
                                            bottom = 24.dp
                                        )
                                ) {
                                    state.lastBetResult?.let { betResult ->
                                        BetResultItem(
                                            betResult = betResult,
                                            modifier = Modifier
                                                .clickable {
                                                    onAction(TrainingScreenAction.OnLastBetResultClicked)
                                                }
                                        )
                                    }
                                }
                            }


                            // Time and amount
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.Bottom)
                                ) {
                                    // Time options
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = minutesDropDownOpened,
                                        enter = fadeIn() + expandIn(
                                            expandFrom = Alignment.BottomCenter,
                                            initialSize = { fullSize ->
                                                IntSize(fullSize.width, 0)
                                            }
                                        ),
                                        exit = shrinkOut(
                                            shrinkTowards = Alignment.BottomCenter,
                                            targetSize = { fullSize ->
                                                IntSize(fullSize.width, 0)
                                            }
                                        ) + fadeOut()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(bottom = 8.dp)
                                                .fillMaxWidth()
                                                .background(
                                                    color = LightBlue,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(vertical = 12.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .onPlaced {
                                                        timeOptionsDividerHeight =
                                                            with(d) { it.size.height.toDp() }
                                                    },
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.seconds, 10),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 0
                                                            seconds = 10
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.seconds, 30),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 0
                                                            seconds = 30
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.minutes, 1),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 1
                                                            seconds = 0
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                            }
                                            Spacer(
                                                modifier = Modifier
                                                    .background(color = Color(47, 61, 92))
                                                    .width(1.dp)
                                                    .height(timeOptionsDividerHeight)
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.minutes, 3),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 3
                                                            seconds = 0
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.minutes, 10),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 10
                                                            seconds = 0
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.minutes, 30),
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .safeSingleClick {
                                                            minutes = 30
                                                            seconds = 0
                                                            minutesDropDownOpened = false
                                                        }
                                                )
                                            }
                                        }
                                    }

                                    // Selected time
                                    Row(
                                        modifier = Modifier
                                            .onPlaced {
                                                timeAndAmoutHeight =
                                                    with(d) { it.size.height.toDp() }
                                            }
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = LightBlue)
                                            .safeSingleClick {
                                                minutesDropDownOpened = !minutesDropDownOpened
                                            }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.time),
                                                style = DefaultTextStyle.copy(
                                                    color = ColorLightGray,
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamilyAvenirRegular
                                                ),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                            Text(
                                                text = "$minutesStr:$secondsStr",
                                                style = DefaultTextStyle.copy(
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontFamily = FontFamilyAvenirRegular
                                                ),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_drop_down),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .size(24.dp)
                                                .align(Alignment.CenterVertically)
                                                .rotate(
                                                    if (minutesDropDownOpened) 0f else 180f
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.Bottom)
                                ) {
                                    // Amount options
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = amountDropDownOpened,
                                        enter = fadeIn() + expandIn(
                                            expandFrom = Alignment.BottomCenter,
                                            initialSize = { fullSize ->
                                                IntSize(fullSize.width, 0)
                                            }
                                        ),
                                        exit = shrinkOut(
                                            shrinkTowards = Alignment.BottomCenter,
                                            targetSize = { fullSize ->
                                                IntSize(fullSize.width, 0)
                                            }
                                        ) + fadeOut()
                                    ) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .padding(bottom = 8.dp)
                                                .background(
                                                    color = LightBlue,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(
                                                    vertical = 12.dp,
                                                    horizontal = 32.dp
                                                ),
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            items(amountOptions) { itemAmount ->
                                                Text(
                                                    text = "$$itemAmount",
                                                    style = DefaultTextStyle.copy(
                                                        color = ColorLightGray,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamilyAvenirRegular
                                                    ),
                                                    modifier = Modifier
                                                        .safeSingleClick {
                                                            onAction(
                                                                TrainingScreenAction.OnNewPreparedAmountSelected(
                                                                    itemAmount
                                                                )
                                                            )
                                                            amountDropDownOpened = false
                                                        }
                                                )
                                            }
                                        }
                                    }

                                    // Selected amount
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = LightBlue)
                                            .safeSingleClick {
                                                amountDropDownOpened = !amountDropDownOpened
                                            }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.amount),
                                                style = DefaultTextStyle.copy(
                                                    color = ColorLightGray,
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamilyAvenirRegular
                                                ),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                            Text(
                                                text = "$${state.preparedAmount}",
                                                style = DefaultTextStyle.copy(
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontFamily = FontFamilyAvenirRegular
                                                ),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_drop_down),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .size(24.dp)
                                                .align(Alignment.CenterVertically)
                                                .rotate(
                                                    if (amountDropDownOpened) 0f else 180f
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        // Bet buttons
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.earings),
                                    style = DefaultTextStyle.copy(
                                        color = ColorLightGray,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamilyAvenirRegular
                                    ),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = state.formattedCoefficient,
                                    style = DefaultTextStyle.copy(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamilyAvenirRegular
                                    ),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = payout,
                                    style = DefaultTextStyle.copy(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamilyAvenirRegular
                                    ),
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            UpButton(
                                modifier = Modifier.weight(1f),
                                enabled = state.bet == null && state.preparedAmount != 0 && !(minutes == 0 && seconds == 0),
                                onCallClicked = {
                                    onAction(
                                        TrainingScreenAction.OnUpClicked(
                                            minutes,
                                            seconds,
                                            state.preparedAmount
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            DownButton(
                                modifier = Modifier.weight(1f),
                                enabled = state.bet == null && state.preparedAmount != 0 && !(minutes == 0 && seconds == 0),
                                onPutClicked = {
                                    onAction(
                                        TrainingScreenAction.OnDownClicked(
                                            minutes,
                                            seconds,
                                            state.preparedAmount
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                //Assets chooser
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.showType == ShowType.ASSET_CHOOSER,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.assets),
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamilyAvenirRegular
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        onAction(TrainingScreenAction.OnShowTypeChange(ShowType.CHART))
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        LazyColumn {
                            itemsIndexed(
                                items = state.instruments,
                                key = { _, instrument -> instrument.name }
                            ) { index, instrument ->
                                val gap = 8.dp
                                AssetItem(
                                    instrument = instrument,
                                    isSelected = state.selectedInstrument?.name == instrument.name,
                                    coefficient = state.formattedCoefficient,
                                    modifier = Modifier
                                        .padding(
                                            top = if (index == 0) 0.dp else gap / 2,
                                            bottom = if (index == state.instruments.size - 1) 0.dp else gap / 2,
                                        )
                                        .safeSingleClick {
                                            onAction(
                                                TrainingScreenAction.OnInstrumentSelected(
                                                    instrument
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    }
                }

                //Trade history
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.showType == ShowType.TRADE_HISTORY,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.trade_history),
                                style = DefaultTextStyle.copy(
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamilyAvenirRegular
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        onAction(TrainingScreenAction.OnShowTypeChange(ShowType.CHART))
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        LazyColumn {
                            itemsIndexed(
                                items = state.betResults
                            ) { index, betResult ->
                                val gap = 24.dp
                                TradeHistoryItem(
                                    betResult = betResult,
                                    modifier = Modifier
                                        .padding(
                                            top = if (index == 0) 0.dp else gap / 2,
                                            bottom = if (index == state.instruments.size - 1) 0.dp else gap / 2,
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TrainingScreenChartPreview() {
    val instruments = listOf(
        "GBP", "EUR", "JPY", "CHF", "AUD", "NZD", "RUB"
    ).map { it.toInstrument() }

    TradingOrangeTheme {
        TrainingScreen(
            state = TrainingScreenState(
                balanceFormatted = "44.826,12 $",
                showType = ShowType.CHART,
                instruments = instruments,
                selectedInstrument = "EUR".toInstrument(),
                formattedCoefficient = "+80%",
                candles = listOf(
                    CandleStick(
                        maxValue = 14.808f,
                        minValue = 7.404f,
                        startValue = 8f,
                        endValue = 9f,
                        startTime = 0L,
                        endTime = 0L,
                        1
                    ),
                    CandleStick(
                        maxValue = 0.492f,
                        minValue = 0.246f,
                        startValue = 0.30f,
                        endValue = 0.40f,
                        startTime = 0L,
                        endTime = 0L,
                        1
                    ),
                    CandleStick(
                        maxValue = 0.084f,
                        minValue = 0.042f,
                        startValue = 0.048f,
                        endValue = 0.056f,
                        startTime = 0L,
                        endTime = 0L,
                        1
                    ),
                    CandleStick(
                        maxValue = 3.972f,
                        minValue = 1.986f,
                        startValue = 2f,
                        endValue = 3.1f,
                        startTime = Calendar.getInstance().timeInMillis - 40 * 1000,
                        endTime = Calendar.getInstance().timeInMillis,
                        1
                    ),
                ),
                bet = Bet(
                    startTime = Calendar.getInstance().timeInMillis - 30 * 1000,
                    rateOnStart = 3.6f,
                    timeSeconds = 120,
                    amount = 10,
                    type = BetType.UP
                ),
                lastBetResult = BetResult(
                    instrument = "EUR".toInstrument(),
                    betAmount = 50f,
                    result = 75f,
                    time = Calendar.getInstance().timeInMillis
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun TrainingScreenAssetsChoosePreview() {
    val instruments = listOf(
        "GBP", "EUR", "JPY", "CHF", "AUD", "NZD", "RUB"
    ).map { it.toInstrument() }
    TradingOrangeTheme {
        TrainingScreen(
            state = TrainingScreenState(
                balanceFormatted = "44.826,12 $",
                showType = ShowType.ASSET_CHOOSER,
                instruments = instruments,
                selectedInstrument = "EUR".toInstrument(),
                formattedCoefficient = "+80%"
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun TrainingScreenBetHistoryPreview() {
    val betResults = mutableListOf<BetResult>()
    val instruments = listOf(
        "GBP", "EUR", "JPY", "CHF", "AUD", "NZD", "RUB"
    ).map { it.toInstrument() }
    repeat(6) {
        val amount = betResults.count().toFloat() * 2.4f
        val win = Random.nextBoolean()
        betResults.add(
            BetResult(
                instrument = instruments.random(),
                betAmount = amount,
                result = if (win) amount else 0 - amount,
                time = Calendar.getInstance().timeInMillis
            )
        )
    }
    TradingOrangeTheme {
        TrainingScreen(
            state = TrainingScreenState(
                balanceFormatted = "44.826,12 $",
                betResults = betResults,
                showType = ShowType.TRADE_HISTORY
            ),
            onAction = {}
        )
    }
}