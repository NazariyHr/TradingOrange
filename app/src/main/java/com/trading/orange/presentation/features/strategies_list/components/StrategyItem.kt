package com.trading.orange.presentation.features.strategies_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
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
import com.trading.orange.domain.model.StrategyArticle
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import kotlinx.coroutines.launch

/**
 * Created by nazar at 12.08.2024
 */
@Composable
fun StrategyItem(
    strategyArticle: StrategyArticle,
    modifier: Modifier = Modifier
) {
    val d = LocalDensity.current
    val scope = rememberCoroutineScope()
    var imageWidth by remember {
        mutableStateOf(0.dp)
    }
    var image by remember {
        mutableStateOf<ByteArray?>(null)
    }
    DisposableEffect(key1 = strategyArticle.imageDataProvider) {
        val loadImageJob = scope.launch {
            image = strategyArticle.imageDataProvider?.provideImage(
                heightPx = with(d) { imageWidth.roundToPx().toLong() },
                widthPx = with(d) { imageWidth.roundToPx().toLong() }
            )
        }
        onDispose {
            loadImageJob.cancel()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .onPlaced {
                imageWidth = with(d) { it.size.width.toDp() }
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .size(imageWidth)
                .clip(RoundedCornerShape(8.dp)),
            model = image,
            contentDescription = null,
            error = painterResource(id = R.drawable.no_image),
            placeholder = painterResource(id = R.drawable.no_image),
            contentScale = ContentScale.Crop
        )

        Text(
            text = strategyArticle.title,
            style = DefaultTextStyle.copy(
                color = Color.White,
                fontFamily = FontFamilyAvenirRegular,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .width(imageWidth)
                .padding(top = 12.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun StrategyItemPreview() {
    TradingOrangeTheme {
        StrategyItem(
            strategyArticle = StrategyArticle(
                id = 0,
                title = "Bollinge R’S friend (BB, MACD)",
                type = "Trend",
                timeframe = "5 - 15 s",
                assets = "Currency pairs",
                difficultyTitle = "Easy",
                difficultyColorHex = "#37C757",
                text = "Bollinger Bands (BB) is a world-famous momentum indicator created by financial analyst John Bollinger. If you are familiar with BB, you know how good it is in indicating trends and flats. This strategy lets you improve your trading experience by inviting Bollinger’s “best friend” — MACD.",
                imageDataProvider = null
            ),
            modifier = Modifier
        )
    }
}