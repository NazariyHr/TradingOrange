package com.trading.orange.presentation.features.training.components.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.trading.orange.presentation.common.theme.ColorLightGray
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular

val ScaleValueTextStyle = TextStyle(
    fontSize = 12.sp,
    fontFamily = FontFamilyAvenirRegular,
    color = ColorLightGray,
    background = Color.Transparent
)

val ScaleCurrValueTextStyle = TextStyle(
    fontSize = 12.sp,
    fontFamily = FontFamilyAvenirRegular,
    color = Color.White,
    background = Color.Transparent,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)