package com.trading.orange.data.server.dto

import com.google.gson.annotations.SerializedName
import com.trading.orange.domain.model.ImageProvider
import com.trading.orange.domain.model.NewsArticle

data class NewsArticleDto(
    val title: String,
    @SerializedName("text_content")
    val textContent: String,
    @SerializedName("link_content")
    val linkContent: String
)

fun NewsArticleDto.toNewsArticle(imageDataProvider: ImageProvider? = null): NewsArticle {
    return NewsArticle(
        title = title,
        text = textContent,
        imageDataProvider = imageDataProvider,
        link = linkContent
    )
}