package com.trading.orange.domain.use_case

import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): List<NewsArticle> {
        return newsRepository.getNews()
    }
}