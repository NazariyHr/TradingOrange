package com.trading.orange.presentation.features.training.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Created by nazar at 13.08.2024
 */
@Composable
fun BetTimerItem(
    startTime: Long,
    endTime: Long,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currTime by rememberSaveable {
        mutableLongStateOf(0L)
    }
    val progressPercent by remember {
        derivedStateOf {
            val fullLength = (endTime - startTime)
            val millisecondsInOnePercent = fullLength / 100L

            val leftMilliseconds = endTime - currTime
            val passedMilliseconds = fullLength - leftMilliseconds

            (passedMilliseconds / millisecondsInOnePercent).toFloat() / 100f
        }
    }
    val leftSeconds by remember {
        derivedStateOf {
            (endTime - currTime) / 1000
        }
    }
    val leftMinutes by remember {
        derivedStateOf {
            leftSeconds / 60
        }
    }
    val restSeconds by remember {
        derivedStateOf {
            leftSeconds - (leftMinutes * 60)
        }
    }
    val leftMinutesStr by remember {
        derivedStateOf {
            (if (leftMinutes < 10) "0" else "") + "$leftMinutes"
        }
    }
    val restSecondsStr by remember {
        derivedStateOf {
            (if (restSeconds < 10) "0" else "") + "$restSeconds"
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

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = LightBlue
            )
            .drawBehind {
                drawRect(
                    color = Color(47, 61, 92),
                    topLeft = Offset.Zero,
                    size = Size(
                        width = size.width * progressPercent,
                        height = size.height
                    )
                )
            }
            .padding(top = 5.dp, bottom = 3.dp, start = 30.dp, end = 30.dp)
    ) {
        Text(
            text = "$leftMinutesStr:$restSecondsStr",
            style = DefaultTextStyle
        )
    }
}

@Preview
@Composable
private fun BetTimerItemPreview() {
    val startTime = Calendar.getInstance().timeInMillis - 1000 * 60 * 2
    val endTime = startTime + 1000 * 60 * 2 + 1000 * 40
    TradingOrangeTheme {
        BetTimerItem(
            startTime = startTime,
            endTime = endTime
        )
    }
}