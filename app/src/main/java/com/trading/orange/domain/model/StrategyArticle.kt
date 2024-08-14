package com.trading.orange.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.trading.orange.presentation.common.theme.toColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class StrategyArticle(
    val id: Int,
    val title: String,
    val type: String,
    val timeframe: String,
    val assets: String,
    val difficultyTitle: String,
    val difficultyColorHex: String,
    val text: String,
    val imageDataProvider: ImageProvider?
) : Parcelable {
    fun getDifficultyColor(): Color {
        return difficultyColorHex.toColor()
    }
}