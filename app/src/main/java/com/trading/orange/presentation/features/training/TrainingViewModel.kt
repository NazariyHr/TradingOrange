package com.trading.orange.presentation.features.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.use_case.balance.GetBalanceFlowUseCase
import com.trading.orange.domain.use_case.balance.ResetBalanceUseCase
import com.trading.orange.presentation.common.utils.formatBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getBalanceFlowUseCase: GetBalanceFlowUseCase,
    private val resetBalanceUseCase: ResetBalanceUseCase
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
    }

    fun onAction(action: TrainingScreenAction) {
        when (action) {
            is TrainingScreenAction.OnInstrumentSelected -> {
                viewModelScope.launch {
                    selectedInstrument.emit(action.instrument)
                    stateValue = stateValue.copy(
                        selectedInstrument = action.instrument
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
                // todo: implement bet logic
            }
            is TrainingScreenAction.OnUpClicked -> {
                // todo: implement bet logic
            }
        }
    }
}