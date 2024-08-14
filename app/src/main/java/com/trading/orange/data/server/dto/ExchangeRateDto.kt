package com.trading.orange.data.server.dto

import com.google.gson.annotations.SerializedName

data class ExchangeRateDto(
    @SerializedName("c")
    val name: String,
    @SerializedName("p")
    val price: Float,
    @SerializedName("ch")
    val change: Float,
    @SerializedName("chp")
    val changePercent: Float
)