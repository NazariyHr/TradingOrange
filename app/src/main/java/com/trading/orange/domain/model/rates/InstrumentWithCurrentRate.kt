package com.trading.orange.domain.model.rates

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstrumentWithCurrentRate(
    val instrument: Instrument,
    val value: Float,
    val lastChangePercent: Float
) : Parcelable