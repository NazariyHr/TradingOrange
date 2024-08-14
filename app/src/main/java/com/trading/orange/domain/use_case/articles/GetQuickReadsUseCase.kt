package com.trading.orange.domain.use_case.articles

import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.domain.repository.ArticlesRepository
import javax.inject.Inject

class GetQuickReadsUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    suspend operator fun invoke(): List<QuickReadArticle> {
        return articlesRepository.getQuickReads()
    }
}