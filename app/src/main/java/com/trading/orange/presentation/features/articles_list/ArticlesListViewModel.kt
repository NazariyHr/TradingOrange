package com.trading.orange.presentation.features.articles_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.GetQuickReadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getQuickReadsUseCase: GetQuickReadsUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: ArticlesListScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<ArticlesListScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, ArticlesListScreenState())

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope
            .launch(Dispatchers.IO) {
                val quickReads = getQuickReadsUseCase()
                stateValue = stateValue.copy(
                    quickReads = quickReads
                )
            }
    }
}