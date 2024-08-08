package com.trading.orange.presentation.features.strategies_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StrategiesListScreenState(
    val title: String = "Strategies List"
) : Parcelable