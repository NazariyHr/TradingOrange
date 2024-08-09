package com.trading.orange.presentation.features.discover

import android.os.Parcelable
import com.trading.orange.domain.model.NewsArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscoverScreenState(
    val news: List<NewsArticle>? = null
) : Parcelable