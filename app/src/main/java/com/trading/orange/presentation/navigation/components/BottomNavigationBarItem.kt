package com.trading.orange.presentation.navigation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.R
import com.trading.orange.presentation.common.theme.ColorLightGray
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

@Composable
fun NavBarItem(
    @DrawableRes icon: Int,
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = icon),
            tint = if (isSelected) ColorOrange else ColorLightGray,
            contentDescription = null
        )

        Text(
            text = title,
            style = DefaultTextStyle.copy(
                color = if (isSelected) ColorOrange else ColorLightGray,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .padding(top = 2.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@Preview
@Composable
private fun NavBarItemPreview() {
    TradingOrangeTheme {
        NavBarItem(
            icon = R.drawable.ic_menu_discover,
            title = "Discover",
            isSelected = true,
            modifier = Modifier
        )
    }
}