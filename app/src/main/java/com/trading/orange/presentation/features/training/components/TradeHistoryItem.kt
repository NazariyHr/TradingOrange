package com.trading.orange.presentation.features.training.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.trading.orange.presentation.common.utils.formatResult
import com.trading.orange.presentation.common.utils.formatTime
import java.util.Calendar

/**
 * Created by nazar at 12.08.2024
 */
@Composable
fun TradeHistoryItem(
    betResult: BetResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "USD/" + betResult.instrument.name,
                style = DefaultTextStyle.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamilyAvenirHeavy
                ),
                modifier = Modifier
            )
            Row {
                Image(
                    painter = painterResource(
                        id = if (betResult.result < 0f) R.drawable.ic_arrow_loose else R.drawable.ic_arrow_win
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = betResult.betAmount.formatResult(),
                    style = DefaultTextStyle.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamilyAvenirRegular
                    ),
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(
                text = betResult.result.formatResult(withSign = true),
                style = DefaultTextStyle.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamilyAvenirRegular,
                    color = if (betResult.result < 0f) ColorRed else ColorGreen
                ),
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = betResult.time.formatTime(),
                style = DefaultTextStyle.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamilyAvenirHeavy
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview
@Composable
private fun TradeHistoryItemWinPreview() {
    TradeHistoryItem(
        betResult = BetResult(
            instrument = "JPY".toInstrument(),
            betAmount = 10f,
            result = 17f,
            time = Calendar.getInstance().timeInMillis
        )
    )
}

@Preview
@Composable
private fun TradeHistoryItemLoosePreview() {
    TradeHistoryItem(
        betResult = BetResult(
            instrument = "JPY".toInstrument(),
            betAmount = 10f,
            result = -10f,
            time = Calendar.getInstance().timeInMillis
        )
    )
}