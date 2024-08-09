package com.trading.orange.presentation.features.discover

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.FontFamilyAvenirLight
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.discover.components.NewsItem
import com.trading.orange.presentation.navigation.Screen

@Composable
fun DiscoverScreenRoot(
    navController: NavController,
    viewModel: DiscoverViewModel =
        hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    DiscoverScreen(
        state = state,
        onAllNewsClicked = {
            navController.navigate(Screen.NewsList)
        },
        onStrategiesClicked = {
            navController.navigate(Screen.StrategiesList)
        },
        onArticlesClicked = {
            navController.navigate(Screen.ArticlesList)
        },
        onNewsArticleClicked = { newsArticle ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsArticle.link))
            context.startActivity(browserIntent)
        }
    )
}

@Composable
private fun DiscoverScreen(
    state: DiscoverScreenState,
    onAllNewsClicked: () -> Unit,
    onStrategiesClicked: () -> Unit,
    onArticlesClicked: () -> Unit,
    onNewsArticleClicked: (NewsArticle) -> Unit
) {
    val d = LocalDensity.current
    var screenWidth by remember {
        mutableStateOf(0.dp)
    }
    MainScreensLayout(
        paddingTop = 16.dp,
        paddingStart = 0.dp,
        paddingEnd = 0.dp
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .onPlaced {
                    screenWidth = with(d) { it.size.width.toDp() }
                }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "News",
                    style = DefaultTextStyle.copy(
                        color = Color.White,
                        fontFamily = FontFamilyAvenirHeavy,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!state.news.isNullOrEmpty()) {
                    Text(
                        text = "See all",
                        style = DefaultTextStyle.copy(
                            color = ColorOrange,
                            fontFamily = FontFamilyAvenirLight,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .safeSingleClick {
                                onAllNewsClicked()
                            }
                    )
                }
            }

            if (state.news == null) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 14.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = ColorOrange
                    )
                }
            }

            if (state.news != null) {
                LazyRow(
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp)
                ) {
                    itemsIndexed(
                        state.news,
                        key = { _, newsArticle -> newsArticle.title }
                    ) { index, article ->
                        val gap = 12.dp
                        NewsItem(
                            newsArticle = article,
                            modifier = Modifier
                                .padding(
                                    start = if (index == 0) 0.dp else gap / 2,
                                    end = if (index == state.news.size - 1) 16.dp else gap / 2
                                )
                                .width(screenWidth - 16.dp - 16.dp)
                                .safeSingleClick {
                                    onNewsArticleClicked(article)
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
private fun DiscoverScreenPreview() {
    val news = mutableListOf<NewsArticle>()

    repeat(2) {
        news.add(
            NewsArticle(
                title = "Fed guidance, BOE, Meta's results, Apple - what's moving markets",
                text = "The U.S. Federal Reserve left interest rates unchanged at the conclusion of its latest policy-setting meeting on Wednesday, as widely expected, but acknowledged recent progress on inflation, raising investor hopes that the central bank could begin cutting rates in the near future.",
                imageDataProvider = null,
                link = ""
            )
        )
    }

    TradingOrangeTheme {
        DiscoverScreen(
            state = DiscoverScreenState(
                news = null//news
            ),
            onAllNewsClicked = {},
            onStrategiesClicked = {},
            onArticlesClicked = {},
            onNewsArticleClicked = {}
        )
    }
}