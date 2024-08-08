package com.trading.orange.presentation.features.discover

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscoverScreenState(
    val title: String = "Discover"
) : Parcelable