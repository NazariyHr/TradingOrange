package com.trading.orange.presentation.features.signals

import android.os.Parcelable
import com.trading.orange.domain.model.rates.Signal
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignalsScreenState(
    val signals: List<Signal> = listOf()
) : Parcelable