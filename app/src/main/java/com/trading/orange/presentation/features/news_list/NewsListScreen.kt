package com.trading.orange.presentation.features.news_list

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.R
import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.discover.components.NewsItem

@Composable
fun NewsListScreenRoot(
    navController: NavController,
    viewModel: NewsListViewModel =
        hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    NewsListScreen(
        state = state,
        navigateUp = {
            navController.navigateUp()
        },
        onNewsArticleClicked = { newsArticle ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsArticle.link))
            context.startActivity(browserIntent)
        }
    )
}

@Composable
private fun NewsListScreen(
    state: NewsListScreenState,
    navigateUp: () -> Unit,
    onNewsArticleClicked: (NewsArticle) -> Unit
) {
    val d = LocalDensity.current
    var backIconHeight by remember {
        mutableStateOf(0.dp)
    }
    MainScreensLayout(
        paddingTop = 16.dp,
        paddingBottom = 18.dp
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
                    text = "News",
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

            if (state.news == null) {
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
            if (state.news != null) {
                LazyColumn {
                    itemsIndexed(
                        items = state.news,
                        key = { _, newsArticle -> newsArticle.title }
                    ) { index, article ->
                        val paddingTop = 16.dp
                        val gap = 16.dp
                        NewsItem(
                            newsArticle = article,
                            modifier = Modifier
                                .padding(
                                    top = if (index == 0) paddingTop else gap / 2,
                                    bottom = if (index == state.news.size - 1) 0.dp else gap / 2
                                )
                                .fillMaxWidth()
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
private fun NewsListScreenWithNewsPreview() {
    val news = mutableListOf<NewsArticle>()
    repeat(2) {
        news.add(
            NewsArticle(
                title = "Fed guidance, BOE, Meta's results, Apple - what's moving markets" + " " + news.size,
                text = "The U.S. Federal Reserve left interest rates unchanged at the conclusion of its latest policy-setting meeting on Wednesday, as widely expected, but acknowledged recent progress on inflation, raising investor hopes that the central bank could begin cutting rates in the near future.",
                imageDataProvider = null,
                link = ""
            )
        )
    }

    TradingOrangeTheme {
        NewsListScreen(
            state = NewsListScreenState(
                news = news
            ),
            navigateUp = {},
            onNewsArticleClicked = {}
        )
    }
}

@Preview
@Composable
private fun NewsListScreenEmptyNewsPreview() {
    TradingOrangeTheme {
        NewsListScreen(
            state = NewsListScreenState(),
            navigateUp = {},
            onNewsArticleClicked = {}
        )
    }
}