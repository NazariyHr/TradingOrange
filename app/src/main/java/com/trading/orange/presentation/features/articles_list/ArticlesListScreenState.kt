package com.trading.orange.presentation.features.articles_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticlesListScreenState(
    val title: String = "Article List"
) : Parcelable