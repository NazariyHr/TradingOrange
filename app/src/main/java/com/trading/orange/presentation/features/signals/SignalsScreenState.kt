package com.trading.orange.presentation.features.signals

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignalsScreenState(
    val title: String = "Signals"
) : Parcelable