package com.trading.orange.presentation.features.strategy_details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StrategyDetailsScreenState(
    val title: String = "Strategy Details"
) : Parcelable