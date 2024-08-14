package com.trading.orange.domain.repository

import com.trading.orange.domain.model.NewsArticle

interface NewsRepository {
    suspend fun getNews(): List<NewsArticle>
}