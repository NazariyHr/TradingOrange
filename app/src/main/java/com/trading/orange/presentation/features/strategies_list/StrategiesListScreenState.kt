package com.trading.orange.presentation.features.strategies_list

import android.os.Parcelable
import com.trading.orange.domain.model.StrategyArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class StrategiesListScreenState(
    val strategies: List<StrategyArticle>? = null
) : Parcelable