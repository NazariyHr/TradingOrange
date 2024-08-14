package com.trading.orange.domain.use_case.articles

import com.trading.orange.domain.model.StrategyArticle
import com.trading.orange.domain.repository.ArticlesRepository
import javax.inject.Inject

class GetStrategyByIdUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    suspend operator fun invoke(strategyArticleId: Int): StrategyArticle? {
        return articlesRepository.getStrategies().firstOrNull { it.id == strategyArticleId }
    }
}