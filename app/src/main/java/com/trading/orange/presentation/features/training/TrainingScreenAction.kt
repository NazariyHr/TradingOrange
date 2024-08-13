package com.trading.orange.presentation.features.training

import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.presentation.features.training.components.chart.ChartDrawType

sealed class TrainingScreenAction {
    data object OnRefreshBalanceClicked : TrainingScreenAction()
    data class OnShowTypeChange(val showType: ShowType) : TrainingScreenAction()
    data class OnInstrumentSelected(val instrument: Instrument) : TrainingScreenAction()
    data class OnChartDrawTypeChange(val chartDrawType: ChartDrawType) : TrainingScreenAction()
    data class OnUpClicked(val minutes: Int, val seconds: Int, val amount: Int) :
        TrainingScreenAction()

    data class OnDownClicked(val minutes: Int, val seconds: Int, val amount: Int) :
        TrainingScreenAction()

    data object OnLastBetResultClicked : TrainingScreenAction()
}