package com.trading.orange.presentation.features.signals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.model.rates.toBet
import com.trading.orange.domain.use_case.rates.AddBetUseCase
import com.trading.orange.domain.use_case.rates.GetSignalsListFlowUseCase
import com.trading.orange.domain.use_case.rates.IncrementSignalCopiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignalsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getSignalsListFlowUseCase: GetSignalsListFlowUseCase,
    private val addBetUseCase: AddBetUseCase,
    private val incrementSignalCopiesUseCase: IncrementSignalCopiesUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: SignalsScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<SignalsScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, SignalsScreenState())

    init {
        getSignalsListFlowUseCase()
            .onEach { signals ->
                stateValue = stateValue.copy(
                    signals = signals
                )
            }
            .launchIn(viewModelScope)
    }

    fun onCopySignalClicked(signal: Signal) {
        addBetUseCase(signal.instrument.name, signal.toBet())
        incrementSignalCopiesUseCase(signalId = signal.id)
    }
}