package com.trading.orange.presentation.features.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetType
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.use_case.balance.GetBalanceFlowUseCase
import com.trading.orange.domain.use_case.balance.ResetBalanceUseCase
import com.trading.orange.domain.use_case.rates.AddBetUseCase
import com.trading.orange.domain.use_case.rates.GetAllBetResultsFlowUseCase
import com.trading.orange.domain.use_case.rates.GetBetFlowUseCase
import com.trading.orange.domain.use_case.rates.GetCoefficientFlowUseCase
import com.trading.orange.domain.use_case.rates.GetInstrumentsFlowUseCase
import com.trading.orange.domain.use_case.rates.GetLastNotSeenBetResultFlowUseCase
import com.trading.orange.domain.use_case.rates.GetPreparedBetAmountFlowUseCase
import com.trading.orange.domain.use_case.rates.GetRatesCandlesFlowUseCase
import com.trading.orange.domain.use_case.rates.GetRatesFlowUseCase
import com.trading.orange.domain.use_case.rates.SetBetResultsAsSeenUseCase
import com.trading.orange.domain.use_case.rates.SetPreparedBetAmountUseCase
import com.trading.orange.presentation.common.utils.formatBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getBalanceFlowUseCase: GetBalanceFlowUseCase,
    private val resetBalanceUseCase: ResetBalanceUseCase,
    getCoefficientFlowUseCase: GetCoefficientFlowUseCase,
    getInstrumentsFlowUseCase: GetInstrumentsFlowUseCase,
    getRatesCandlesFlowUseCase: GetRatesCandlesFlowUseCase,
    getRatesFlowUseCase: GetRatesFlowUseCase,
    getBetFlowUseCase: GetBetFlowUseCase,
    private val addBetUseCase: AddBetUseCase,
    getLastNotSeenBetResultFlowUseCase: GetLastNotSeenBetResultFlowUseCase,
    getAllBetResultsFlowUseCase: GetAllBetResultsFlowUseCase,
    private val setBetResultsAsSeenUseCase: SetBetResultsAsSeenUseCase,
    getPreparedBetAmountFlowUseCase: GetPreparedBetAmountFlowUseCase,
    private val setPreparedBetAmountUseCase: SetPreparedBetAmountUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: TrainingScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<TrainingScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, TrainingScreenState())

    private val selectedInstrument = MutableSharedFlow<Instrument?>(1)

    init {
        getBalanceFlowUseCase()
            .onEach { newBalance ->
                stateValue = stateValue.copy(
                    balanceFormatted = newBalance.formatBalance(),
                    balance = newBalance
                )
            }
            .launchIn(viewModelScope)

        getCoefficientFlowUseCase()
            .onEach { newCoefficient ->
                stateValue = stateValue.copy(
                    formattedCoefficient = "+" + (newCoefficient * 100).toInt() + "%",
                    coefficient = newCoefficient
                )
            }
            .launchIn(viewModelScope)

        getInstrumentsFlowUseCase()
            .onEach { instruments ->
                stateValue = stateValue.copy(
                    instruments = instruments
                )
                if (stateValue.selectedInstrument == null) {
                    selectedInstrument.emit(instruments.first())
                }
            }
            .launchIn(viewModelScope)

        selectedInstrument
            .onEach { instrument ->
                stateValue = stateValue.copy(
                    selectedInstrument = instrument
                )
            }
            .launchIn(viewModelScope)

        selectedInstrument
            .filterNotNull()
            .flatMapLatest { instrument ->
                getRatesCandlesFlowUseCase(instrument.name)
            }
            .onEach { candles ->
                stateValue = stateValue.copy(
                    candles = candles
                )
            }
            .launchIn(viewModelScope)

        selectedInstrument
            .filterNotNull()
            .flatMapLatest { instrument ->
                getRatesFlowUseCase(instrument.name)
            }
            .onEach { rates ->
                stateValue = stateValue.copy(
                    rates = rates
                )
            }
            .launchIn(viewModelScope)

        selectedInstrument
            .filterNotNull()
            .flatMapLatest { instrument ->
                getBetFlowUseCase(instrument.name)
            }
            .onEach { bet ->
                stateValue = stateValue.copy(
                    bet = bet
                )
            }
            .launchIn(viewModelScope)

        getLastNotSeenBetResultFlowUseCase()
            .onEach { lastResult ->
                stateValue = stateValue.copy(
                    lastBetResult = lastResult
                )
            }
            .launchIn(viewModelScope)

        getAllBetResultsFlowUseCase()
            .onEach { allBetResults ->
                stateValue = stateValue.copy(
                    betResults = allBetResults
                )
            }
            .launchIn(viewModelScope)

        getPreparedBetAmountFlowUseCase()
            .onEach { preparedAmount ->
                stateValue = stateValue.copy(
                    preparedAmount = preparedAmount
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: TrainingScreenAction) {
        when (action) {
            is TrainingScreenAction.OnInstrumentSelected -> {
                viewModelScope.launch {
                    selectedInstrument.emit(action.instrument)
                    stateValue = stateValue.copy(
                        selectedInstrument = action.instrument,
                        showType = ShowType.CHART
                    )
                }
            }

            TrainingScreenAction.OnRefreshBalanceClicked -> {
                viewModelScope.launch {
                    resetBalanceUseCase()
                }
            }

            is TrainingScreenAction.OnShowTypeChange -> {
                stateValue = stateValue.copy(
                    showType = action.showType
                )
            }

            is TrainingScreenAction.OnChartDrawTypeChange ->
                stateValue = stateValue.copy(
                    chartDrawType = action.chartDrawType
                )

            is TrainingScreenAction.OnDownClicked -> {
                stateValue.selectedInstrument?.name?.let { instrumentName ->
                    val lastCandle = stateValue.candles.last()
                    addBetUseCase(
                        instrumentName,
                        Bet(
                            lastCandle.endTime,
                            lastCandle.endValue,
                            action.minutes * 60 + action.seconds,
                            action.amount,
                            BetType.DOWN
                        )
                    )
                }
            }

            is TrainingScreenAction.OnUpClicked -> {
                stateValue.selectedInstrument?.name?.let { instrumentName ->
                    val lastCandle = stateValue.candles.last()
                    addBetUseCase(
                        instrumentName,
                        Bet(
                            lastCandle.endTime,
                            lastCandle.endValue,
                            action.minutes * 60 + action.seconds,
                            action.amount,
                            BetType.UP
                        )
                    )
                }
            }

            TrainingScreenAction.OnLastBetResultClicked -> {
                setBetResultsAsSeenUseCase()
            }

            is TrainingScreenAction.OnNewPreparedAmountSelected -> {
                setPreparedBetAmountUseCase(action.newPreparedAmount)
            }
        }
    }
}