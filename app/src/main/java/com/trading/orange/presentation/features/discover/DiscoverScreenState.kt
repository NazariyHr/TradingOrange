package com.trading.orange.presentation.features.discover

import android.os.Parcelable
import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.domain.model.StrategyArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscoverScreenState(
    val news: List<NewsArticle>? = null,
    val strategies: List<StrategyArticle>? = null,
    val quickReads: List<QuickReadArticle>? = null
) : Parcelable