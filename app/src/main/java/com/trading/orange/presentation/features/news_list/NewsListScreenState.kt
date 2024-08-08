package com.trading.orange.presentation.features.news_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsListScreenState(
    val title: String = "News list"
) : Parcelable