package com.trading.orange.presentation.features.strategy_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.articles.GetStrategyByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrategyDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getStrategyByIdUseCase: GetStrategyByIdUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: StrategyDetailsScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<StrategyDetailsScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, StrategyDetailsScreenState())

    private val strategyArticleId: MutableStateFlow<Int?> = MutableStateFlow(null)

    init {
        strategyArticleId
            .filterNotNull()
            .map { id ->
                getStrategyByIdUseCase(id)
            }
            .onEach { article ->
                stateValue = stateValue.copy(
                    strategyArticle = article
                )
            }
            .launchIn(viewModelScope)
    }

    fun setId(articleId: Int) {
        viewModelScope.launch {
            strategyArticleId.emit(articleId)
        }
    }
}