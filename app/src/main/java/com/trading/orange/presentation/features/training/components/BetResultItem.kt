package com.trading.orange.presentation.features.training.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.R
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.ColorRed
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.common.utils.formatResult
import java.util.Calendar

@Composable
fun BetResultItem(
    betResult: BetResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (betResult.result >= 0) Modifier.background(ColorGreen)
                else Modifier.background(ColorRed)
            )
            .padding(12.dp)
    ) {
        Image(
            modifier = Modifier
                .height(24.dp)
                .width(36.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = betResult.instrument.getIcon()),
            contentDescription = null
        )
        Text(
            text = "USD/" + betResult.instrument.name,
            style = DefaultTextStyle.copy(
                fontSize = 16.sp,
                fontFamily = FontFamilyAvenirRegular
            ),
            modifier = Modifier
                .padding(start = 12.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = betResult.result.formatResult(withSign = true).replace("$", ""),
            style = DefaultTextStyle.copy(
                fontSize = 16.sp,
                fontFamily = FontFamilyAvenirHeavy
            ),
            modifier = Modifier
                .padding(start = 6.dp)
                .align(Alignment.CenterVertically)
        )
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun BetResultItemPreviewNegative() {
    TradingOrangeTheme {
        BetResultItem(
            betResult = BetResult(
                instrument = "EUR".toInstrument(),
                betAmount = 10f,
                result = -10f,
                time = Calendar.getInstance().timeInMillis
            )
        )
    }
}

@Preview
@Composable
private fun BetResultItemPreviewPositive() {
    TradingOrangeTheme {
        BetResultItem(
            betResult = BetResult(
                instrument = "EUR".toInstrument(),
                betAmount = 10f,
                result = 17f,
                time = Calendar.getInstance().timeInMillis
            )
        )
    }
}