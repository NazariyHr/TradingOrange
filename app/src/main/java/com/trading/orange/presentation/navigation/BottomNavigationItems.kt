package com.trading.orange.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trading.orange.R

enum class BottomNavigationItems(
    @DrawableRes val icon: Int,
    @StringRes val titleId: Int,
    val screenToNavigate: ScreenGroup
) {
    DISCOVER(R.drawable.ic_menu_discover, R.string.bottom_nav_menu_discover, ScreenGroup.DiscoverScreensGroup),
    TRAINING(R.drawable.ic_menu_training, R.string.bottom_nav_menu_training, ScreenGroup.TrainingScreensGroup),
    SIGNALS(R.drawable.ic_menu_signals, R.string.bottom_nav_menu_signals, ScreenGroup.SignalsScreensGroup),
    CONTACT_US(R.drawable.ic_menu_contact_us, R.string.bottom_nav_menu_contact_us, ScreenGroup.ContactUsScreensGroup)
}