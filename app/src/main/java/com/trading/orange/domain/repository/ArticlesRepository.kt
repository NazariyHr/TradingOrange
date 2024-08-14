package com.trading.orange.domain.repository

import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.domain.model.StrategyArticle

interface ArticlesRepository {
    suspend fun getStrategies(): List<StrategyArticle>
    suspend fun getQuickReads(): List<QuickReadArticle>
}