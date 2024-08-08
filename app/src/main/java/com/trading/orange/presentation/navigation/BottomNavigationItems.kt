package com.trading.orange.presentation.navigation

import androidx.annotation.DrawableRes
import com.trading.orange.R

enum class BottomNavigationItems(
    @DrawableRes val icon: Int,
    val title: String,
    val screenToNavigate: ScreenGroup
) {
    DISCOVER(R.drawable.ic_menu_discover, "Discover", ScreenGroup.DiscoverScreensGroup),
    TRAINING(R.drawable.ic_menu_training, "Training", ScreenGroup.TrainingScreensGroup),
    SIGNALS(R.drawable.ic_menu_signals, "Signals", ScreenGroup.SignalsScreensGroup),
    CONTACT_US(R.drawable.ic_menu_contact_us, "Contact us", ScreenGroup.ContactUsScreensGroup)
}