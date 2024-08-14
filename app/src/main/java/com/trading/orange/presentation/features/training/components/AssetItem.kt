package com.trading.orange.presentation.features.training.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

/**
 * Created by nazar at 12.08.2024
 */
@Composable
fun AssetItem(
    instrument: Instrument,
    isSelected: Boolean,
    coefficient: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(
                if (isSelected) Modifier.background(
                    color = LightBlue,
                    shape = RoundedCornerShape(8.dp)
                )
                else Modifier
            )
            .padding(12.dp)
    ) {
        Image(
            modifier = Modifier
                .height(24.dp)
                .width(36.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = instrument.getIcon()),
            contentDescription = null
        )
        Text(
            text = "USD/" + instrument.name,
            style = DefaultTextStyle.copy(
                fontSize = 16.sp,
                fontFamily = FontFamilyAvenirRegular
            ),
            modifier = Modifier
                .padding(start = 12.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = coefficient,
            style = DefaultTextStyle.copy(
                fontSize = 16.sp,
                fontFamily = FontFamilyAvenirRegular,
                color = ColorGreen
            ),
            modifier = Modifier
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun AssetItemSelectedPreview() {
    TradingOrangeTheme {
        AssetItem(
            instrument = "GBP".toInstrument(),
            coefficient = "80%",
            isSelected = true
        )
    }
}

@Preview
@Composable
private fun AssetItemUnselectedPreview() {
    TradingOrangeTheme {
        AssetItem(
            instrument = "GBP".toInstrument(),
            coefficient = "80%",
            isSelected = false
        )
    }
}