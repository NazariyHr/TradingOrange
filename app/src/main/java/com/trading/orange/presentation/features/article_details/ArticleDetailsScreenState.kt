package com.trading.orange.presentation.features.article_details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleDetailsScreenState(
    val title: String = "Article Details"
) : Parcelable