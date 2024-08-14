package com.trading.orange.data.local_assets.dto

import com.google.gson.annotations.SerializedName
import com.trading.orange.domain.model.ImageProvider
import com.trading.orange.domain.model.StrategyArticle

data class StrategyArticleDto(
    @SerializedName("image_file_name")
    val imageFileName: String,
    val title: String,
    val type: String,
    val timeframe: String,
    val assets: String,
    @SerializedName("difficulty_title")
    val difficultyTitle: String,
    @SerializedName("difficulty_color")
    val difficultyColorHex: String,
    val text: String
)

fun StrategyArticleDto.toStrategyArticle(
    id: Int,
    imageDataProvider: ImageProvider? = null
): StrategyArticle {
    return StrategyArticle(
        id = id,
        title = title,
        type = type,
        timeframe = timeframe,
        assets = assets,
        difficultyTitle = difficultyTitle,
        difficultyColorHex = difficultyColorHex,
        text = text,
        imageDataProvider = imageDataProvider
    )
}