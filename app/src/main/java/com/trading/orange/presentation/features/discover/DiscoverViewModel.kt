package com.trading.orange.presentation.features.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.news.GetNewsUseCase
import com.trading.orange.domain.use_case.articles.GetQuickReadsUseCase
import com.trading.orange.domain.use_case.articles.GetStrategiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getNewsUseCase: GetNewsUseCase,
    private val getStrategiesUseCase: GetStrategiesUseCase,
    private val getQuickReadsUseCase: GetQuickReadsUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: DiscoverScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<DiscoverScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, DiscoverScreenState())

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope
            .launch(Dispatchers.IO) {
                val news = getNewsUseCase()
                val strategies = getStrategiesUseCase()
                val quickReads = getQuickReadsUseCase()
                stateValue = stateValue.copy(
                    news = news,
                    strategies = strategies,
                    quickReads = quickReads
                )
            }
    }
}