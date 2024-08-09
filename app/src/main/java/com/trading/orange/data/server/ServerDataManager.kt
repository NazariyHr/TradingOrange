package com.trading.orange.data.server

import com.trading.orange.data.server.dto.NewsArticleDto

class ServerDataManager(
    private val serverApi: ServerApi
) {
    private val cachedNewsData: MutableMap<String, List<NewsArticleDto>> = mutableMapOf()

    suspend fun getNews(
        search: String
    ): List<NewsArticleDto> {
        return cachedNewsData[search] ?: serverApi.getNews(search)
            .also { cachedNewsData[search] = it }
    }
}