package com.trading.orange.domain.model.rates

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bet(
    val startTime: Long,
    val rateOnStart: Float,
    val timeSeconds: Int,
    val amount: Int,
    val type: BetType
) : Parcelable

enum class BetType {
    CALL, PUT
}