package com.trading.orange.presentation.features.discover.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.trading.orange.R
import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import kotlinx.coroutines.launch

/**
 * Created by nazar at 09.08.2024
 */
@Composable
fun NewsItem(
    newsArticle: NewsArticle,
    modifier: Modifier = Modifier
) {
    val d = LocalDensity.current
    val scope = rememberCoroutineScope()
    var imageWidth by remember {
        mutableLongStateOf(0L)
    }
    var image by remember {
        mutableStateOf<ByteArray?>(null)
    }
    DisposableEffect(key1 = newsArticle.imageDataProvider, key2 = imageWidth) {
        val loadImageJob = scope.launch {
            image = newsArticle.imageDataProvider?.provideImage(
                heightPx = with(d) { 220.dp.roundToPx().toLong() },
                widthPx = imageWidth
            )
        }
        onDispose {
            loadImageJob.cancel()
        }
    }

    Column(
        modifier = modifier
            .onPlaced {
                imageWidth = it.size.width.toLong()
            }
            .fillMaxWidth()
    ) {
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

        Text(
            text = newsArticle.title,
            style = DefaultTextStyle.copy(
                color = Color.White,
                fontFamily = FontFamilyAvenirRegular,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun NewsItemPreview() {
    TradingOrangeTheme {
        NewsItem(
            newsArticle = NewsArticle(
                title = "Fed guidance, BOE, Meta's results, Apple - what's moving markets",
                text = "The U.S. Federal Reserve left interest rates unchanged at the conclusion of its latest policy-setting meeting on Wednesday, as widely expected, but acknowledged recent progress on inflation, raising investor hopes that the central bank could begin cutting rates in the near future.",
                imageDataProvider = null,
                link = ""
            ),
            modifier = Modifier
        )
    }
}