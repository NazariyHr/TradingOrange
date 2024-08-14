package com.trading.orange.data.server

import com.trading.orange.data.server.dto.ExchangeRateDto
import com.trading.orange.data.server.dto.NewsArticleDto

class ServerDataManager(
    private val serverApi: ServerApi
) {
    private var cachedRatesData: List<ExchangeRateDto>? = null
    private val cachedNewsData: MutableMap<String, List<NewsArticleDto>> = mutableMapOf()

    suspend fun getNews(
        search: String
    ): List<NewsArticleDto> {
        return cachedNewsData[search] ?: serverApi.getNews(search)
            .also { cachedNewsData[search] = it }
    }

    suspend fun getExchangeRates(): List<ExchangeRateDto> {
        return cachedRatesData ?: serverApi.getExchangeRates()
            .also { cachedRatesData = it }
    }
}