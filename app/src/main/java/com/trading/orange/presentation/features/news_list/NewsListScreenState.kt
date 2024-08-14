package com.trading.orange.presentation.features.news_list

import android.os.Parcelable
import com.trading.orange.domain.model.NewsArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsListScreenState(
    val news: List<NewsArticle>? = null
) : Parcelable