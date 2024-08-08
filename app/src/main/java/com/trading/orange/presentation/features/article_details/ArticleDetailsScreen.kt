package com.trading.orange.presentation.features.article_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

@Composable
fun ArticleDetailsScreenRoot(
    navController: NavController,
    viewModel: ArticleDetailsViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ArticleDetailsScreen(
        state = state
    )
}

@Composable
private fun ArticleDetailsScreen(
    state: ArticleDetailsScreenState
) {
    MainScreensLayout {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = state.title,
                style = DefaultTextStyle.copy(
                    color = Color.White,
                    fontFamily = FontFamilyAvenirHeavy,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Preview
@Composable
private fun ArticleDetailsScreenPreview() {
    TradingOrangeTheme {
        ArticleDetailsScreen(
            state = ArticleDetailsScreenState()
        )
    }
}