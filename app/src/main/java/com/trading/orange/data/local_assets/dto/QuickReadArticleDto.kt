package com.trading.orange.data.local_assets.dto

import com.google.gson.annotations.SerializedName
import com.trading.orange.domain.model.ImageProvider
import com.trading.orange.domain.model.QuickReadArticle

data class QuickReadArticleDto(
    @SerializedName("image_file_name")
    val imageFileName: String,
    val title: String,
    val text: String
)

fun QuickReadArticleDto.toQuickReadArticle(
    id: Int,
    imageDataProvider: ImageProvider? = null
): QuickReadArticle {
    return QuickReadArticle(
        id = id,
        title = title,
        text = text,
        imageDataProvider = imageDataProvider
    )
}
