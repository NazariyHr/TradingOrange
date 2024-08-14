package com.trading.orange.presentation.common.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.R
import com.trading.orange.presentation.common.components.MainScreensLayout

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val FontFamilyRobotoRegular = FontFamily(Font(R.font.roboto_regular))

val FontFamilyAvenirLight = FontFamily(Font(R.font.avenir_light))
val FontFamilyAvenirRegular = FontFamily(Font(R.font.avenir_regular))
val FontFamilyAvenirBook = FontFamily(Font(R.font.avenir_book))
val FontFamilyAvenirHeavy = FontFamily(Font(R.font.avenir_heavy))
val FontFamilyAvenirBlack = FontFamily(Font(R.font.avenir_black))

val DefaultTextStyle = TextStyle(
    fontFamily = FontFamilyAvenirRegular,
    fontSize = 14.sp,
    color = Color.White,
    background = Color.Transparent,
)

@Preview
@Composable
private fun FontsPreviewOnMainBackgroundColor() {
    TradingOrangeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MainBgColor)
        ) {
            MainScreensLayout {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Roboto Regular",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyRobotoRegular,
                            fontSize = 30.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Avenir Light",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirLight,
                            fontSize = 30.sp
                        )
                    )
                    Text(
                        text = "Avenir Regular",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirRegular,
                            fontSize = 30.sp
                        )
                    )
                    Text(
                        text = "Avenir Book",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirBook,
                            fontSize = 30.sp
                        )
                    )
                    Text(
                        text = "Avenir Heavy",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirHeavy,
                            fontSize = 30.sp
                        )
                    )
                    Text(
                        text = "Avenir Black",
                        style = DefaultTextStyle.copy(
                            color = Color.White,
                            fontFamily = FontFamilyAvenirBlack,
                            fontSize = 30.sp
                        )
                    )
                }
            }
        }
    }
}