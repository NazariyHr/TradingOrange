package com.trading.orange.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class Screen {
    @Parcelize
    @Serializable
    data object Discover : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object Training : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object Signals : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object ContactUs : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object NewsList : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object StrategiesList : Screen(), Parcelable

    @Parcelize
    @Serializable
    data object ArticlesList : Screen(), Parcelable

    @Serializable
    @Parcelize
    data class StrategyDetails(val strategyId: Int) : Screen(), Parcelable

    @Serializable
    @Parcelize
    data class ArticleDetails(val articleId: Int) : Screen(), Parcelable
}

sealed class ScreenGroup : Screen() {
    @Serializable
    data object DiscoverScreensGroup : ScreenGroup() {
        override fun getFirstScreenOfGroup(): Screen = Discover
    }

    @Serializable
    data object TrainingScreensGroup : ScreenGroup() {
        override fun getFirstScreenOfGroup(): Screen = Training
    }

    @Serializable
    data object SignalsScreensGroup : ScreenGroup() {
        override fun getFirstScreenOfGroup(): Screen = Signals
    }

    @Serializable
    data object ContactUsScreensGroup : ScreenGroup() {
        override fun getFirstScreenOfGroup(): Screen = ContactUs
    }

    abstract fun getFirstScreenOfGroup(): Screen
}