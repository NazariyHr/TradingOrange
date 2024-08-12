package com.trading.orange.presentation.features.news_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: NewsListScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<NewsListScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, NewsListScreenState())

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope
            .launch(Dispatchers.IO) {
                val news = getNewsUseCase()
                stateValue = stateValue.copy(
                    news = news
                )
            }
    }
}