package com.trading.orange.domain.model.rates

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BetResult(
    val instrument: Instrument,
    val result: Float,
    val time: Long
) : Parcelable
