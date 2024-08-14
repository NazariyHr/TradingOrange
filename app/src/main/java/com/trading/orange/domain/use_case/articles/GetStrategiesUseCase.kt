package com.trading.orange.domain.use_case.articles

import com.trading.orange.domain.model.StrategyArticle
import com.trading.orange.domain.repository.ArticlesRepository
import javax.inject.Inject

class GetStrategiesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    suspend operator fun invoke(): List<StrategyArticle> {
        return articlesRepository.getStrategies()
    }
}