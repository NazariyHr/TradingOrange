package com.trading.orange.domain.model.rates

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RateData(
    val time: Long,
    val rateValue: Float
) : Parcelable

/**
 * @param interval - time interval in seconds for forming one candle stick
 */
fun List<RateData>.toCandleSticks(interval: Long = 60): List<CandleStick> {
    val data = this
    if (data.isEmpty()) return emptyList()

    val candleSticks = mutableListOf<CandleStick>()

    val ratesSortedByTime = data.sortedBy { it.time }
    val firstRate = ratesSortedByTime.first()

    if (data.count() == 1) return listOf(
        CandleStick(
            maxValue = firstRate.rateValue,
            minValue = firstRate.rateValue,
            startValue = firstRate.rateValue,
            endValue = firstRate.rateValue,
            startTime = firstRate.time,
            endTime = firstRate.time,
            valuesAmount = 1
        )
    )

    var candleStickMaxValue = firstRate.rateValue
    var candleStickMinValue = firstRate.rateValue
    var candleStickStartValue = firstRate.rateValue
    var previousRateValue = firstRate.rateValue
    var previousRateTime = firstRate.time

    var candleStickStartTime = firstRate.time
    val intervalMillis = interval * 1000

    val rates = ratesSortedByTime.drop(1)
    val lastIndex = rates.lastIndex
    var valuesAmountInCandleStick = 0

    rates.forEachIndexed { index, rateData ->
        val t = rateData.time
        val v = rateData.rateValue
        valuesAmountInCandleStick++

        if (t > candleStickStartTime + intervalMillis) {
            // previous rate was last rate in forming candle stick
            candleSticks.add(
                CandleStick(
                    maxValue = candleStickMaxValue,
                    minValue = candleStickMinValue,
                    startValue = candleSticks.lastOrNull()?.endValue
                        ?: candleStickStartValue,
                    endValue = previousRateValue,
                    startTime = candleStickStartTime,
                    endTime = previousRateTime,
                    valuesAmount = valuesAmountInCandleStick
                )
            )
            valuesAmountInCandleStick = 0

            candleStickStartTime = t

            candleStickMaxValue = v
            candleStickMinValue = v
            candleStickStartValue = v
        }

        if (candleStickMaxValue < v) candleStickMaxValue = v
        if (candleStickMinValue > v) candleStickMinValue = v

        previousRateValue = v
        previousRateTime = rateData.time

        if (index == lastIndex) {
            candleSticks.add(
                CandleStick(
                    maxValue = candleStickMaxValue,
                    minValue = candleStickMinValue,
                    startValue = candleSticks.lastOrNull()?.endValue
                        ?: candleStickStartValue,
                    endValue = v,
                    startTime = candleStickStartTime,
                    endTime = rateData.time,
                    valuesAmount = valuesAmountInCandleStick
                )
            )
            valuesAmountInCandleStick = 0
        }
    }
    return candleSticks
}