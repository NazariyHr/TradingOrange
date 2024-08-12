package com.trading.orange.presentation.features.article_details

import android.os.Parcelable
import com.trading.orange.domain.model.QuickReadArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleDetailsScreenState(
    val quickReadArticle: QuickReadArticle? = null
) : Parcelable