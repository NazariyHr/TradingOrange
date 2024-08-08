package com.trading.orange.presentation.common.theme

import androidx.compose.ui.graphics.Color

val DarkBlue = Color(29, 36, 57)
val LightBlue = Color(40, 51, 78)

val ColorGreen = Color(55, 199, 87)
val ColorRed = Color(250, 87, 60)
val ColorOrange = Color(245, 125, 51)

val ColorLightGray = Color(146, 169, 201)
val ColorGray = Color(88, 102, 130)
val ColorGrayLighter = Color(94, 109, 137)

val MainBgColor = DarkBlue

fun String.toColor(): Color = Color(android.graphics.Color.parseColor(this))