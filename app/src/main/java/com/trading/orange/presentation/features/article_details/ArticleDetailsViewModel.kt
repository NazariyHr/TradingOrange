package com.trading.orange.presentation.features.article_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.orange.domain.use_case.GetQuickReadByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getQuickReadByIdUseCase: GetQuickReadByIdUseCase
) : ViewModel() {
    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: ArticleDetailsScreenState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<ArticleDetailsScreenState>(STATE_KEY)!!
        }
    val state = savedStateHandle.getStateFlow(STATE_KEY, ArticleDetailsScreenState())

    private val quickReadArticleId: MutableStateFlow<Int?> = MutableStateFlow(null)

    init {
        quickReadArticleId
            .filterNotNull()
            .map { id ->
                getQuickReadByIdUseCase(id)
            }
            .onEach { article ->
                stateValue = stateValue.copy(
                    quickReadArticle = article
                )
            }
            .launchIn(viewModelScope)
    }

    fun setId(articleId: Int) {
        viewModelScope.launch {
            quickReadArticleId.emit(articleId)
        }
    }
}