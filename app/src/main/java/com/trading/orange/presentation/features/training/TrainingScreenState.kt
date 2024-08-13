package com.trading.orange.presentation.features.training

import android.os.Parcelable
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.presentation.features.training.components.chart.ChartDrawType
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainingScreenState(
    val showType: ShowType = ShowType.CHART,
    val chartDrawType: ChartDrawType = ChartDrawType.CANDLES,
    val balanceFormatted: String = "",
    val balance: Float = 0f,
    val coefficient: Float = 0f,
    val formattedCoefficient: String = "",
    val instruments: List<Instrument> = emptyList(),
    val selectedInstrument: Instrument? = null,
    val betResults: List<BetResult> = emptyList(),
    val bet: Bet? = null
) : Parcelable

enum class ShowType {
    CHART,
    ASSET_CHOOSER,
    TRADE_HISTORY
}