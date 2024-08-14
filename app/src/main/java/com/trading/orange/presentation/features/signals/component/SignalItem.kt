package com.trading.orange.presentation.features.signals.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.R
import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.model.rates.SignalType
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.ColorLightGray
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.ColorRed
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.LightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

/**
 * Created by nazar at 13.08.2024
 */
@Composable
fun SignalItem(
    signal: Signal,
    onCopySignalClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currTime by rememberSaveable {
        mutableLongStateOf(0L)
    }
    val endTime by remember {
        derivedStateOf {
            signal.startTime + signal.timeSeconds * 1000L
        }
    }
    val progressPercent by remember {
        derivedStateOf {
            val fullLength = (endTime - signal.startTime)
            val millisecondsInOnePercent = fullLength / 100L

            val leftMilliseconds = endTime - currTime
            val passedMilliseconds = fullLength - leftMilliseconds

            (passedMilliseconds / millisecondsInOnePercent).toFloat() / 100f
        }
    }

    LaunchedEffect(Unit) {
        currTime = Calendar.getInstance().timeInMillis
        scope.launch {
            while (true) {
                delay(1000L)
                currTime = Calendar.getInstance().timeInMillis
            }
        }
    }

    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "USD/" + signal.instrument.name,
                style = DefaultTextStyle.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamilyAvenirHeavy
                ),
                modifier = Modifier
            )
            Text(
                text = "$" + signal.amountForBet,
                style = DefaultTextStyle.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(id = R.string.copied_times, signal.copies),
                style = DefaultTextStyle.copy(
                    fontSize = 12.sp,
                    color = ColorLightGray
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = signal.getSignalTypeIcon()),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "$",
                style = DefaultTextStyle.copy(
                    fontSize = 14.sp,
                    color = if (signal.isUp()) ColorGreen else ColorRed
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Spacer(
                modifier = modifier
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        color = LightBlue
                    )
                    .drawBehind {
                        drawRect(
                            color = Color(88, 102, 130),
                            topLeft = Offset.Zero,
                            size = Size(
                                width = size.width * progressPercent,
                                height = size.height
                            )
                        )
                    }
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .then(
                        if (signal.availableToBet && abs(endTime - currTime) > 2000 && signal.amountForBet > 0)
                            Modifier.safeSingleClick {
                                onCopySignalClicked()
                            }
                        else Modifier
                    )
                    .fillMaxWidth()
                    .then(
                        if (signal.availableToBet && abs(endTime - currTime) > 2000 && signal.amountForBet > 0) {
                            Modifier.background(
                                color = ColorOrange,
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else {
                            Modifier.background(
                                color = Color(217, 217, 217),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    )
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.copy_signal),
                    style = DefaultTextStyle.copy(
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun SignalItemDoubleUpPreview() {
    val startTime = Calendar.getInstance().timeInMillis - 1000 * 60 * 2
    val timeSeconds = 60 * 2 + 40
    SignalItem(
        Signal(
            id = 0,
            instrument = "GBP".toInstrument(),
            currInstrumentRate = 45f,
            amountForBet = 100,
            copies = 34,
            type = SignalType.DOUBLE_UP,
            startTime = startTime,
            timeSeconds = timeSeconds,
            availableToBet = true
        ),
        onCopySignalClicked = {}
    )
}

@Preview
@Composable
private fun SignalItemBatNotAvailablePreview() {
    val startTime = Calendar.getInstance().timeInMillis - 1000 * 60 * 2
    val timeSeconds = 60 * 2 + 40
    SignalItem(
        Signal(
            id = 0,
            instrument = "GBP".toInstrument(),
            currInstrumentRate = 45f,
            amountForBet = 100,
            copies = 34,
            type = SignalType.DOWN,
            startTime = startTime,
            timeSeconds = timeSeconds,
            availableToBet = false
        ),
        onCopySignalClicked = {}
    )
}