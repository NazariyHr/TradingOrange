package com.trading.orange.presentation.features.strategies_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.GetStrategiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrategiesListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getStrategiesUseCase: GetStrategiesUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: StrategiesListScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<StrategiesListScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, StrategiesListScreenState())

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope
            .launch(Dispatchers.IO) {
                val strategies = getStrategiesUseCase()
                stateValue = stateValue.copy(
                    strategies = strategies
                )
            }
    }
}