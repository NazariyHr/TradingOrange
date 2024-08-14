package com.trading.orange.data.local_assets.dto

import com.google.gson.annotations.SerializedName

data class ArticlesDto(
    val strategies: List<StrategyArticleDto>,
    @SerializedName("quick_reads")
    val quickReads: List<QuickReadArticleDto>
)
