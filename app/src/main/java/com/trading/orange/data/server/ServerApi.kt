package com.trading.orange.data.server

import com.trading.orange.data.server.dto.NewsArticleDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    companion object {
        const val BASE_URL = "https://66b35f7d0700c4e1ddde.appwrite.global/"

        private const val DEBUG_KEY = "debug"

        val searchParameters = listOf(
            "META",
            "TSLA",
            "GOOGL",
            "AMZN",
            "AAPL",
            "MSFT"
        )
    }

    @GET("3490/")
    suspend fun getNews(
        @Query("search") search: String,
        @Query("mode") type: String = "news",
        @Query("key") key: String = DEBUG_KEY
    ): List<NewsArticleDto>
}