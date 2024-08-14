package com.trading.orange.presentation.features.article_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.trading.orange.R
import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.modifiers.toHtmlString
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import kotlinx.coroutines.launch

@Composable
fun ArticleDetailsScreenRoot(
    articleId: Int,
    navController: NavController,
    viewModel: ArticleDetailsViewModel =
        hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = articleId) {
        viewModel.setId(articleId)
    }
    ArticleDetailsScreen(
        state = state,
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun ArticleDetailsScreen(
    state: ArticleDetailsScreenState,
    navigateUp: () -> Unit
) {
    val d = LocalDensity.current
    val scope = rememberCoroutineScope()
    var imageWidth by remember {
        mutableLongStateOf(0L)
    }
    var image by remember {
        mutableStateOf<ByteArray?>(null)
    }
    DisposableEffect(key1 = state.quickReadArticle?.imageDataProvider, key2 = imageWidth) {
        val loadImageJob = scope.launch {
            image = state.quickReadArticle?.imageDataProvider?.provideImage(
                heightPx = with(d) { 220.dp.roundToPx().toLong() },
                widthPx = imageWidth
            )
        }
        onDispose {
            loadImageJob.cancel()
        }
    }

    MainScreensLayout(
        paddingTop = 16.dp,
        paddingBottom = 0.dp
    ) {
        Column(
            modifier = Modifier
                .onPlaced {
                    imageWidth = it.size.width.toLong()
                }
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .safeSingleClick {
                        navigateUp()
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                if (state.quickReadArticle != null) {
                    AsyncImage(
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        model = image,
                        contentDescription = null,
                        error = painterResource(id = R.drawable.no_image),
                        placeholder = painterResource(id = R.drawable.no_image),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.quickReadArticle.title,
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirRegular,
                            fontSize = 16.sp
                        )
                    )

                    Spacer(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .height(1.dp)
                            .background(LightBlue)
                            .fillMaxWidth()
                    )

                    Text(
                        text = state.quickReadArticle.text.toHtmlString(),
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirRegular,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArticleDetailsScreenPreview() {
    TradingOrangeTheme {
        ArticleDetailsScreen(
            state = ArticleDetailsScreenState(
                quickReadArticle = QuickReadArticle(
                    id = 1,
                    title = "Simple trading book",
                    text = "The U.S. Federal Reserve left interest rates...",
                    imageDataProvider = null
                )
            ),
            navigateUp = {}
        )
    }
}