package com.trading.orange.presentation.features.articles_list.components

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
import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import kotlinx.coroutines.launch

/**
 * Created by nazar at 12.08.2024
 */
@Composable
fun QuickReadItem(
    quickReadArticle: QuickReadArticle,
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
    DisposableEffect(key1 = quickReadArticle.imageDataProvider) {
        val loadImageJob = scope.launch {
            image = quickReadArticle.imageDataProvider?.provideImage(
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
            text = quickReadArticle.title,
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
        QuickReadItem(
            quickReadArticle = QuickReadArticle(
                id = 1,
                title = "Simple trading book",
                text = "The U.S. Federal Reserve left interest rates...",
                imageDataProvider = null
            ),
            modifier = Modifier
        )
    }
}