package com.trading.orange.presentation.features.training

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainingScreenState(
    val title: String = "Training"
) : Parcelable