package com.trading.orange.presentation.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.trading.orange.presentation.common.modifiers.safeSingleClick
import com.trading.orange.presentation.common.theme.LightBlue
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.navigation.BottomNavigationItems
import com.trading.orange.presentation.navigation.Screen
import kotlin.enums.EnumEntries

@Composable
fun BottomNavigationBar(
    navigationBarItems: EnumEntries<BottomNavigationItems>,
    selectedItemIndex: Int,
    onItemClick: (Screen, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            .fillMaxWidth()
            .background(LightBlue)
    ) {
        navigationBarItems.forEachIndexed { index, item ->
            NavBarItem(
                icon = item.icon,
                title = item.title,
                isSelected = selectedItemIndex == index,
                modifier = Modifier
                    .weight(1f)
                    .safeSingleClick(
                        interactionSource = null,
                        indication = null
                    ) {
                        onItemClick(item.screenToNavigate, index)
                    }
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview(modifier: Modifier = Modifier) {
    TradingOrangeTheme {
        BottomNavigationBar(
            BottomNavigationItems.entries,
            0,
            { s, i -> },
            modifier = Modifier.wrapContentHeight()
        )
    }
}