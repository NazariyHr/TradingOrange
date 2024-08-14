package com.trading.orange.presentation.features.articles_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.R
import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.articles_list.components.QuickReadItem
import com.trading.orange.presentation.navigation.Screen

@Composable
fun ArticlesListScreenRoot(
    navController: NavController,
    viewModel: ArticlesListViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ArticlesListScreen(
        state = state,
        navigateUp = {
            navController.navigateUp()
        },
        onArticleClicked = { quickReadArticle ->
            navController.navigate(Screen.ArticleDetails(quickReadArticle.id))
        }
    )
}

@Composable
private fun ArticlesListScreen(
    state: ArticlesListScreenState,
    navigateUp: () -> Unit,
    onArticleClicked: (QuickReadArticle) -> Unit
) {
    val d = LocalDensity.current
    var backIconHeight by remember {
        mutableStateOf(0.dp)
    }
    MainScreensLayout(
        paddingTop = 22.dp,
        paddingBottom = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(backIconHeight)
                        .safeSingleClick {
                            navigateUp()
                        }
                )
                Text(
                    text = stringResource(id = R.string.quick_reads),
                    style = DefaultTextStyle.copy(
                        color = Color.White,
                        fontFamily = FontFamilyAvenirHeavy,
                        fontSize = 20.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .onPlaced {
                            backIconHeight = with(d) { it.size.height.toDp() }
                        }
                )
            }

            if (state.quickReads == null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = ColorOrange
                    )
                }
            }
            if (state.quickReads != null) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        top = 30.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = state.quickReads) { quickReadArticle ->
                        QuickReadItem(
                            quickReadArticle = quickReadArticle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .safeSingleClick {
                                    onArticleClicked(quickReadArticle)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArticlesListScreenPreview() {
    TradingOrangeTheme {
        ArticlesListScreen(
            state = ArticlesListScreenState(),
            navigateUp = {},
            onArticleClicked = {}
        )
    }
}