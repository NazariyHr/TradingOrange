package com.trading.orange.domain.model.rates

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CandleStick(
    val maxValue: Float,
    val minValue: Float,
    val startValue: Float,
    val endValue: Float,
    val startTime: Long,
    val endTime: Long,
    val valuesAmount: Int
) : Parcelable