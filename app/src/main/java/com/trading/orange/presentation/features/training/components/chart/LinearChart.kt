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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.trading.orange.domain.model.rates.RateData
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.ColorRed
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun LinearChart(
    modifier: Modifier = Modifier,
    rates: List<RateData> = listOf(),
    bet: Bet? = null,
    chartVisibleDiapasonCount: Int
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
    LaunchedEffect(fullWidth, drawWidth, scrolledToEndOnInit) {
        if (!scrolledToEndOnInit && fullWidth > 0 && drawWidth > 0) {
            scrollX = 0 - (fullWidth - drawWidth)
            scrolledToEndOnInit = true
        }
    }

    val density = LocalDensity.current
    val dotWidth by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 1.dp.toPx() }
        }
    }
    val insetBeforeScale by remember(LocalDensity.current) {
        derivedStateOf {
            with(density) { 80.dp.toPx() }
        }
    }
    val spaceBetweenDots by remember {
        derivedStateOf {
            drawWidth / chartVisibleDiapasonCount.toFloat()
        }
    }

    val textMeasurer = rememberTextMeasurer()

    if (rates.isNotEmpty()) {
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
                val visibleDots = rates
                    .filterIndexed { index, _ ->
                        val lineX =
                            spaceBetweenDots * index + dotWidth * index + dotWidth / 2
                        lineX in firstVisibleX..lastVisibleX
                    }

                val maxValue = visibleDots.maxOfOrNull { it.rateValue } ?: 0f
                val minValue = visibleDots.minOfOrNull { it.rateValue } ?: 0f
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

                val startYForDots = (textHeight.toFloat() / 2) + (lineHeight / 2)
                val endYForDots =
                    (textHeight.toFloat() / 2) + (lineHeight / 2) + (textHeight * (valuesInScaleToDraw.count() - 1)) + (spaceBetweenDivisions * (valuesInScaleToDraw.count() - 1))
                val pixelsBetweenMaxAndMinDivisionOnScale =
                    endYForDots - startYForDots
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
                            rates.count() * (dotWidth + spaceBetweenDots) + insetBeforeScale

                        if (fullWidth < drawWidth) {
                            fullWidth = size.width
                        }

                        if(!scrolledToEndOnInit) return@translate

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
                            scrollableDistance + dotWidth + spaceBetweenDots
                        val endAutoscrollTriggerDistance = scrollableDistance

                        if (!userIsScrolling && scrollX > scrollableDistance && scrollX in endAutoscrollTriggerDistance..startAutoscrollTriggerDistance) {
                            scrollX = 0 - (fullWidth - size.width)
                        }


                        val lineColor = ColorOrange
                        val p = Path()
                        rates.firstOrNull()?.let { rate ->
                            val lineX = dotWidth / 2
                            val dotIsVisible = lineX in firstVisibleX..lastVisibleX
                            if (!dotIsVisible) return@let
                            val yCoordinate =
                                (rate.rateValue - minValue) / amountChangePerPixel
                            p.moveTo(lineX, endYForDots - yCoordinate)
                        }

                        rates.forEachIndexed { index, rate ->
                            val lineX =
                                spaceBetweenDots * index + dotWidth * index + dotWidth / 2
                            val candleIsVisible = lineX in firstVisibleX..lastVisibleX
                            if (!candleIsVisible) return@forEachIndexed
                            val yCoordinate =
                                (rate.rateValue - minValue) / amountChangePerPixel
                            p.lineTo(lineX, endYForDots - yCoordinate)
                        }

                        drawPath(
                            path = p,
                            color = lineColor,
                            style = Stroke(
                                width = dotWidth,
                                cap = StrokeCap.Round
                            )
                        )

                        bet?.let { b ->
                            val betDot = rates.minByOrNull { abs(b.startTime - it.time) }
                            if (betDot == null) return@let
                            val index = rates.indexOf(betDot)

                            val betXCoordinateAdjusted =
                                spaceBetweenDots * index + dotWidth * index + dotWidth / 2
                            val candleIsVisible =
                                betXCoordinateAdjusted in firstVisibleX..lastVisibleX

                            if (!candleIsVisible) return@let

                            val betYCoordinate =
                                (b.rateOnStart - minValue) / amountChangePerPixel
                            val betYAdjusted = endYForDots - betYCoordinate

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

                        if (rates.last().rateValue in minValue..maxValue) {
                            val currYCoordinate =
                                (rates.last().rateValue - minValue) / amountChangePerPixel
                            val index = rates.count() - 1
                            val currXCoordinate =
                                spaceBetweenDots * index + dotWidth * index + dotWidth / 2
                            val candleIsVisible = currXCoordinate in firstVisibleX..lastVisibleX

                            if (candleIsVisible) {
                                drawLine(
                                    color = ColorOrange,
                                    start = Offset(
                                        x = currXCoordinate,
                                        y = endYForDots - currYCoordinate
                                    ),
                                    end = Offset(
                                        x = lastVisibleX,
                                        y = endYForDots - currYCoordinate
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
                    if (rates.last().rateValue in minValue..maxValue) {
                        val currRate = rates.last().rateValue
                        val endYCoordinate = (currRate - minValue) / amountChangePerPixel
                        drawRoundRect(
                            color = ColorOrange,
                            topLeft = Offset(
                                x = 0f,
                                y = endYForDots - endYCoordinate - (24.dp.toPx() / 2)
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
                                y = endYForDots - endYCoordinate - 8.dp.toPx()
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
private fun LinearChartPreview() {
    var nextRateTime = Calendar.getInstance().timeInMillis
    val firstExchangeRate = 34.55f
    val generatedRates = mutableListOf<RateData>()
    generatedRates.add(
        RateData(
            time = nextRateTime,
            rateValue = firstExchangeRate
        )
    )

    var lastGeneratedRate = firstExchangeRate
    repeat(20 * 60) {
        nextRateTime -= 1000L
        lastGeneratedRate = run {
            val randomValue = Random.nextInt(1..14)
            val randomChangePercent = randomValue.toFloat() / 10000f
            val randomChange = lastGeneratedRate * randomChangePercent
            if (Random.nextBoolean()) {
                lastGeneratedRate - randomChange
            } else {
                lastGeneratedRate + randomChange
            }
        }

        generatedRates.add(
            RateData(
                time = nextRateTime,
                rateValue = lastGeneratedRate
            )
        )
    }

    TradingOrangeTheme {
        LinearChart(
            rates = generatedRates,
            bet = Bet(
                startTime = generatedRates.last().time,
                rateOnStart = generatedRates.last().rateValue,
                timeSeconds = 120,
                amount = 10,
                type = BetType.UP
            ),
            chartVisibleDiapasonCount = 1000
        )
    }
}