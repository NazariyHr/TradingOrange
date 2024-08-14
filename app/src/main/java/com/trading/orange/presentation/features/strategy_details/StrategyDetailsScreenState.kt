package com.trading.orange.presentation.features.strategy_details

import android.os.Parcelable
import com.trading.orange.domain.model.StrategyArticle
import kotlinx.parcelize.Parcelize

@Parcelize
data class StrategyDetailsScreenState(
    val strategyArticle: StrategyArticle? = null
) : Parcelable