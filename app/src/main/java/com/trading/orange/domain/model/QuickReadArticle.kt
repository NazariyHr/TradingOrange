package com.trading.orange.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuickReadArticle(
    val id: Int,
    val title: String,
    val text: String,
    val imageDataProvider: ImageProvider?
) : Parcelable
