package com.trading.orange.domain.use_case.articles

import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.domain.repository.ArticlesRepository
import javax.inject.Inject

class GetQuickReadByIdUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    suspend operator fun invoke(quickReadArticleId: Int): QuickReadArticle? {
        return articlesRepository.getQuickReads().firstOrNull { it.id == quickReadArticleId }
    }
}