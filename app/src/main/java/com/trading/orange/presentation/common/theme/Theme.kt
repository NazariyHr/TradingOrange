package com.trading.orange.presentation.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = LightBlue,
    tertiary = ColorGray,
    background = MainBgColor
)

@Composable
fun TradingOrangeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}