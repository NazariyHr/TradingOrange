package com.trading.orange.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trading.orange.presentation.common.theme.MainBgColor

@Composable
fun MainScreensLayout(
    paddingTop: Dp = 12.dp,
    paddingBottom: Dp = 16.dp,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MainBgColor)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier.padding(
                    top = paddingTop,
                    start = paddingStart,
                    end = paddingEnd,
                    bottom = paddingBottom
                ),
                content = content
            )
        }
    }
}

@Preview
@Composable
private fun MainScreensLayoutPreview() {
    MaterialTheme {
        MainScreensLayout {}
    }
}