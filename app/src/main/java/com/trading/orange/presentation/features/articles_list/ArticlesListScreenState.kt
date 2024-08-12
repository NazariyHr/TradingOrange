package com.trading.orange.presentation.features.articles_list

import android.os.Parcelable
import com.trading.orange.domain.model.QuickReadArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticlesListScreenState(
    val quickReads: List<QuickReadArticle>? = null
) : Parcelable