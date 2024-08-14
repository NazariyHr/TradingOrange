package com.trading.orange.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsArticle(
    val title: String,
    val text: String,
    val link: String,
    val imageDataProvider: ImageProvider?
) : Parcelable