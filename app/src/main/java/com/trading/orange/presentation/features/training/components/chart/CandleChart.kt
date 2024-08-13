package com.trading.orange.presentation.features.training.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetType
import com.trading.orange.domain.model.rates.CandleStick
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.ColorRed
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import java.util.Calendar
import java.util.Locale

@Composable
fun CandleChart(
    modifier: Modifier = Modifier,
    candles: List<CandleStick> = listOf(),
    bet: Bet? = null,
    onNewVisibleDiapasonCount: (Int) -> Unit
) {
    var drawWidth by remember {
        mutableFloatStateOf(0f)
    }
    var drawHeight by remember {
        mutableFloatStateOf(0f)
    }
    var fullWidth by remember {
        mutableFloatStateOf(0f)
    }
    var scaleWidth by remember {
        mutableStateOf(0.dp)
    }
    var scrollX by remember {
        mutableFloatStateOf(0f)
    }
    var userIsScrolling by remember {
        mutableStateOf(false)
    }
    val firstVisibleX by remember(scrollX) {
        derivedStateOf {
            0f - scrollX
        }
    }
    val lastVisibleX by remember(firstVisibleX, drawWidth) {
        derivedStateOf {
            firstVisibleX + drawWidth
        }
    }
    var scrolledToEndOnInit by rememberSaveable {
        mutableStateOf(false)
    }
    val density = LocalDensity.current
    LaunchedEffect(fullWidth, drawWidth, scrolledToEndOnInit) {
        if (!scrolledToEndOnInit && fullWidth > 0 && drawWidth > 0) {
            scrollX = 0 - (fullWidth - drawWidth)
            scrolledToEndOnInit = true
        }
    }

    val candleStickLineWidth by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 1.dp.toPx() }
        }
    }
    val candleStickCandleWidth by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 10.dp.toPx() }
        }
    }
    val spaceBetweenCandles by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 1.dp.toPx() }
        }
    }
    val insetBeforeScale by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 80.dp.toPx() }
        }
    }
    val visibleCandles by remember(candles, firstVisibleX, lastVisibleX) {
        derivedStateOf {
            val filteredCandles = candles
                .filterIndexed { index, _ ->
                    val lineX =
                        spaceBetweenCandles * index + candleStickCandleWidth * index + candleStickCandleWidth / 2
                    lineX in firstVisibleX..lastVisibleX
                }
            onNewVisibleDiapasonCount(filteredCandles.sumOf { it.valuesAmount })
            filteredCandles
        }
    }

    val textMeasurer = rememberTextMeasurer()

    if (candles.isNotEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(true) {
                    awaitEachGesture {
                        awaitFirstDown()
                        userIsScrolling = true

                        do {
                            val event: PointerEvent = awaitPointerEvent()
                            // ACTION_MOVE loop
                        } while (event.changes.any { it.pressed })
                        userIsScrolling = false

                        // ACTION_UP is here
                    }
                }
                .pointerInput(true) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (fullWidth <= drawWidth) {
                            scrollX = 0f
                            return@detectDragGestures
                        }

                        val nextScrollX = scrollX + dragAmount.x
                        scrollX = if (nextScrollX > 0f) {
                            0f
                        } else if (nextScrollX <= 0 - (fullWidth - drawWidth)) {
                            0 - (fullWidth - drawWidth)
                        } else {
                            nextScrollX
                        }
                    }
                }
        ) {
            Box {
                val maxValue = visibleCandles.maxOfOrNull { it.maxValue } ?: 0f
                val minValue = visibleCandles.minOfOrNull { it.minValue } ?: 0f
                val amountOfDivisionsInScale = 6
                val maxMinDifference = maxValue - minValue
                val differenceBetweenDivisions = maxMinDifference / (amountOfDivisionsInScale - 1)

                val valuesInScale = mutableListOf<Float>()
                for (i in 0..<amountOfDivisionsInScale) {
                    when (i) {
                        0 -> {
                            valuesInScale.add(maxValue)
                        }

                        amountOfDivisionsInScale - 1 -> {
                            valuesInScale.add(minValue)
                        }

                        else -> {
                            valuesInScale.add(maxValue - differenceBetweenDivisions * i)
                        }
                    }
                }
                val valuesInScaleToDraw = valuesInScale.map {
                    String
                        .format(Locale.US, "%.4f", it)
                        .replace(".", ",")
                }
                val valuesInScaleToDrawLayoutResults =
                    remember(valuesInScaleToDraw, ScaleValueTextStyle) {
                        valuesInScaleToDraw.map {
                            textMeasurer.measure(it, ScaleValueTextStyle)
                        }
                    }

                val maxTextWidth = valuesInScaleToDrawLayoutResults.maxOf { it.size.width }
                val textHeight = valuesInScaleToDrawLayoutResults.first().size.height
                val txtWidthDp = with(LocalDensity.current) { maxTextWidth.toDp() }
                val lineHeight = with(LocalDensity.current) { 1.dp.toPx() }


                val spaceBetweenDivisions =
                    (drawHeight - (textHeight * amountOfDivisionsInScale)) / (amountOfDivisionsInScale - 1)

                val startYForCandles = (textHeight.toFloat() / 2) + (lineHeight / 2)
                val endYForCandles =
                    (textHeight.toFloat() / 2) + (lineHeight / 2) + (textHeight * (valuesInScaleToDraw.count() - 1)) + (spaceBetweenDivisions * (valuesInScaleToDraw.count() - 1))
                val pixelsBetweenMaxAndMinDivisionOnScale =
                    endYForCandles - startYForCandles
                val maxMinDivisionDifference = maxValue - minValue
                val amountChangePerPixel =
                    maxMinDivisionDifference / pixelsBetweenMaxAndMinDivisionOnScale

                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .graphicsLayer {
                            clip = true
                        }
                ) {
                    translate(
                        left = scrollX
                    ) {
                        drawWidth = size.width - with(density) { scaleWidth.toPx() }
                        drawHeight = size.height

                        fullWidth =
                            candles.count() * (candleStickCandleWidth + spaceBetweenCandles) + insetBeforeScale

                        if (fullWidth < drawWidth) {
                            fullWidth = size.width
                        }

                        if (!scrolledToEndOnInit) return@translate

                        for (i in 0..<valuesInScaleToDraw.count()) {
                            drawLine(
                                color = Color(255f, 255f, 255f, 0.08f),
                                start = Offset(
                                    x = 0f,
                                    y = (textHeight.toFloat() / 2) + (lineHeight / 2) + (textHeight * i) + (spaceBetweenDivisions * i)
                                ),
                                end = Offset(
                                    x = fullWidth,
                                    y = (textHeight.toFloat() / 2) + (lineHeight / 2) + (textHeight * i) + (spaceBetweenDivisions * i)
                                ),
                                strokeWidth = lineHeight
                            )
                        }

                        val step = 190.dp.toPx()
                        var i = step
                        while (i < fullWidth) {
                            drawLine(
                                color = Color(255f, 255f, 255f, 0.08f),
                                start = Offset(
                                    x = i,
                                    y = 0f
                                ),
                                end = Offset(
                                    x = i,
                                    y = size.height
                                ),
                                strokeWidth = lineHeight
                            )
                            i += step
                        }

//                        if (!scrolledToEndOnInit) {
//                            scrollX = 0 - (fullWidth - size.width)
//                            scrolledToEndOnInit = true
//                        }

                        val scrollableDistance = 0 - (fullWidth - size.width)
                        val startAutoscrollTriggerDistance =
                            scrollableDistance + candleStickCandleWidth + spaceBetweenCandles
                        val endAutoscrollTriggerDistance = scrollableDistance

                        if (!userIsScrolling && scrollX > scrollableDistance && scrollX in endAutoscrollTriggerDistance..startAutoscrollTriggerDistance) {
                            scrollX = 0 - (fullWidth - size.width)
                        }

                        candles.forEachIndexed { index, candleStick ->
                            val lineX =
                                spaceBetweenCandles * index + candleStickCandleWidth * index + candleStickCandleWidth / 2
                            val candleIsVisible = lineX in firstVisibleX..lastVisibleX

                            if (!candleIsVisible) return@forEachIndexed

                            val maxYCoordinate =
                                (candleStick.maxValue - minValue) / amountChangePerPixel
                            val minYCoordinate =
                                (candleStick.minValue - minValue) / amountChangePerPixel
                            val startYCoordinate =
                                (candleStick.startValue - minValue) / amountChangePerPixel
                            val endYCoordinate =
                                (candleStick.endValue - minValue) / amountChangePerPixel

                            val candleColor = if (candleStick.endValue >= candleStick.startValue) {
                                ColorGreen
                            } else {
                                ColorRed
                            }

                            drawLine(
                                color = candleColor,
                                start = Offset(
                                    x = lineX,
                                    y = endYForCandles - maxYCoordinate
                                ),
                                end = Offset(
                                    x = lineX,
                                    y = endYForCandles - minYCoordinate
                                ),
                                strokeWidth = candleStickLineWidth,
                                cap = StrokeCap.Round
                            )

                            val startY = endYForCandles - startYCoordinate
                            val endY = endYForCandles - endYCoordinate
                            val height = startY - endY

                            drawRect(
                                color = candleColor,
                                topLeft = Offset(
                                    x = spaceBetweenCandles * index + candleStickCandleWidth * index,
                                    y = endYForCandles - endYCoordinate
                                ),
                                size = Size(
                                    width = candleStickCandleWidth,
                                    height = height
                                )
                            )
                        }

                        bet?.let { b ->
                            val candleStick =
                                candles.find { b.startTime in it.startTime..it.endTime }
                            if (candleStick == null) return@let
                            val index = candles.indexOf(candleStick)

                            val betXCoordinateAdjusted =
                                spaceBetweenCandles * index + candleStickCandleWidth * index + candleStickCandleWidth / 2
                            val candleIsVisible =
                                betXCoordinateAdjusted in firstVisibleX..lastVisibleX

                            if (!candleIsVisible) return@let

                            val betYCoordinate =
                                (b.rateOnStart - minValue) / amountChangePerPixel
                            val betYAdjusted = endYForCandles - betYCoordinate

                            val betColor = if (b.type == BetType.UP) ColorGreen else ColorRed

                            drawCircle(
                                color = betColor,
                                radius = 4.dp.toPx(),
                                center = Offset(
                                    x = betXCoordinateAdjusted,
                                    y = betYAdjusted
                                )
                            )

                            val betRateTxt = "$${b.amount}"
                            val lResult =
                                textMeasurer.measure(
                                    betRateTxt, ScaleCurrValueTextStyle.copy(
                                        fontFamily = FontFamilyAvenirHeavy
                                    )
                                )


                            val p = Path()
                            p.moveTo(
                                x = betXCoordinateAdjusted,
                                y = betYAdjusted
                            )
                            val horizontalPadding = 15.dp.toPx()
                            val verticalPadding = 3.dp.toPx()
                            val flagEdgeWidth = 15.dp.toPx()

                            p.relativeLineTo(
                                dx = -flagEdgeWidth,
                                dy = -(lResult.size.height.toFloat() / 2f + verticalPadding)
                            )
                            p.relativeLineTo(
                                dx = -(horizontalPadding * 2 + lResult.size.width.toFloat()),
                                dy = 0f
                            )
                            p.relativeLineTo(
                                dx = 0f,
                                dy = verticalPadding * 2 + lResult.size.height.toFloat()
                            )
                            p.relativeLineTo(
                                dx = horizontalPadding * 2 + lResult.size.width.toFloat(),
                                dy = 0f
                            )
                            p.relativeLineTo(
                                dx = flagEdgeWidth,
                                dy = -(lResult.size.height.toFloat() / 2f + verticalPadding)
                            )

                            drawPath(
                                path = p,
                                color = betColor
                            )

                            drawText(
                                textMeasurer = textMeasurer,
                                text = betRateTxt,
                                style = ScaleCurrValueTextStyle.copy(
                                    fontFamily = FontFamilyAvenirHeavy
                                ),
                                topLeft = Offset(
                                    x = betXCoordinateAdjusted - lResult.size.width.toFloat() - horizontalPadding - flagEdgeWidth,
                                    y = betYAdjusted - lResult.size.height.toFloat() / 2f
                                ),
                                size = Size(
                                    width = lResult.size.width.toFloat(),
                                    height = lResult.size.height.toFloat()
                                )
                            )
                        }

                        if (candles.last().endValue in minValue..maxValue) {
                            val currYCoordinate =
                                (candles.last().endValue - minValue) / amountChangePerPixel
                            val index = candles.count() - 1
                            val currXCoordinate =
                                spaceBetweenCandles * index + candleStickCandleWidth * index + candleStickCandleWidth / 2
                            val candleIsVisible = currXCoordinate in firstVisibleX..lastVisibleX

                            if (candleIsVisible) {
                                drawLine(
                                    color = ColorOrange,
                                    start = Offset(
                                        x = currXCoordinate,
                                        y = endYForCandles - currYCoordinate
                                    ),
                                    end = Offset(
                                        x = lastVisibleX,
                                        y = endYForCandles - currYCoordinate
                                    ),
                                    strokeWidth = 1.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .onPlaced {
                            scaleWidth = with(density) { it.size.width.toDp() }
                        }
                        .fillMaxHeight()
                        .width(txtWidthDp + (4 + 16).dp)
                        .align(Alignment.CenterEnd)
                ) {
                    val spaceBetweenDivisions = (size.height -
                            (textHeight * amountOfDivisionsInScale)) / (amountOfDivisionsInScale - 1)

                    for (i in 0..<valuesInScaleToDraw.count()) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = valuesInScaleToDraw[i],
                            style = ScaleValueTextStyle,
                            topLeft = Offset(
                                x = 4.dp.toPx(),
                                y = (textHeight * i) + (spaceBetweenDivisions * i),
                            )
                        )
                    }
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                        .width(txtWidthDp + (12 + 12).dp)
                        .align(Alignment.CenterEnd)
                ) {
                    if (candles.last().endValue in minValue..maxValue) {
                        val currRate = candles.last().endValue
                        val endYCoordinate =
                            (currRate - minValue) / amountChangePerPixel
                        drawRoundRect(
                            color = ColorOrange,
                            topLeft = Offset(
                                x = 0f,
                                y = endYForCandles - endYCoordinate - (24.dp.toPx() / 2)
                            ),
                            size = Size(
                                width = size.width,
                                height = 24.dp.toPx()
                            ),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )

                        val currRateTxt = String
                            .format(Locale.US, "%.4f", currRate)
                            .replace(".", ",")
                        val lResult = textMeasurer.measure(currRateTxt, ScaleCurrValueTextStyle)

                        drawText(
                            textMeasurer = textMeasurer,
                            text = currRateTxt,
                            style = ScaleCurrValueTextStyle,
                            topLeft = Offset(
                                x = (size.width - lResult.size.width) / 2,
                                y = endYForCandles - endYCoordinate - 8.dp.toPx()
                            ),
                            size = Size(
                                width = lResult.size.width.toFloat(),
                                height = lResult.size.height.toFloat()
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CandleChartPreview() {
    TradingOrangeTheme {
        CandleChart(
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
                    maxValue = 0.492f,
                    minValue = 0.246f,
                    startValue = 0.30f,
                    endValue = 0.40f,
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
                    maxValue = 0.492f,
                    minValue = 0.246f,
                    startValue = 0.30f,
                    endValue = 0.40f,
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
                    maxValue = 0.492f,
                    minValue = 0.246f,
                    startValue = 0.30f,
                    endValue = 0.40f,
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
            onNewVisibleDiapasonCount = {}
        )
    }
}