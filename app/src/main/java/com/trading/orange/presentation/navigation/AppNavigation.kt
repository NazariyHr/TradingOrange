package com.trading.orange.presentation.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.trading.orange.presentation.common.components.DoubleBackPressToExit
import com.trading.orange.presentation.common.theme.TradingOrangeTheme
import com.trading.orange.presentation.features.article_details.ArticleDetailsScreenRoot
import com.trading.orange.presentation.features.articles_list.ArticlesListScreenRoot
import com.trading.orange.presentation.features.contact_us.ContactUsScreenRoot
import com.trading.orange.presentation.features.discover.DiscoverScreenRoot
import com.trading.orange.presentation.features.news_list.NewsListScreenRoot
import com.trading.orange.presentation.features.signals.SignalsScreenRoot
import com.trading.orange.presentation.features.strategies_list.StrategiesListScreenRoot
import com.trading.orange.presentation.features.strategy_details.StrategyDetailsScreenRoot
import com.trading.orange.presentation.features.training.TrainingScreenRoot
import com.trading.orange.presentation.navigation.components.BottomNavigationBar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.enums.EnumEntries
import kotlin.reflect.typeOf

@Composable
fun AppNavigationRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navigationBarItems = BottomNavigationItems.entries
    val navigationBarItemsNames =
        navigationBarItems.map { it.screenToNavigate::class.qualifiedName }
    val screenRoutesWithoutNavigationBar =
        listOf(
            Screen.NewsList,
            Screen.StrategiesList,
            Screen.ArticlesList,
            Screen.StrategyDetails,
            Screen.ArticleDetails
        ).map { it::class.qualifiedName }

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute by remember {
        derivedStateOf {
            backStackEntry?.destination?.route
        }
    }
    val rootRoute by remember {
        derivedStateOf {
            backStackEntry?.destination?.getRootRoute()
        }
    }

    val currentTabRootScreen by remember {
        derivedStateOf {
            navigationBarItems.find { it.screenToNavigate::class.qualifiedName == rootRoute }?.screenToNavigate
        }
    }
    val showNavigationBar by remember {
        derivedStateOf {
            rootRoute != null &&
                    navigationBarItemsNames.contains(rootRoute) &&
                    !screenRoutesWithoutNavigationBar.any {
                        currentRoute.orEmpty().contains(it.orEmpty().replace(".Companion", ""))
                    }
        }
    }
    val selectedItemIndex by remember {
        derivedStateOf {
            navigationBarItemsNames
                .indexOf(rootRoute)
                .let { index -> if (index >= 0) index else 0 }
        }
    }

    DoubleBackPressToExit()
    AppNavigation(
        showNavigationBar,
        navigationBarItems,
        selectedItemIndex,
        navController,
        currentTabRootScreen,
        modifier
    )
}

@Composable
fun AppNavigation(
    showNavigationBar: Boolean,
    navigationBarItems: EnumEntries<BottomNavigationItems>,
    selectedItemIndex: Int,
    navController: NavHostController,
    currentTabRootScreen: ScreenGroup?,
    modifier: Modifier = Modifier
) {
    var openedScreens by rememberSaveable {
        mutableStateOf(listOf<Screen>())
    }
    val d = LocalDensity.current
    val systemNavBars = WindowInsets.navigationBars
    val systemNavBarHeight by remember(LocalDensity.current) {
        derivedStateOf {
            with(d) { systemNavBars.getBottom(d).toDp() }
        }
    }

    Column(
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = ScreenGroup.DiscoverScreensGroup,
            modifier = Modifier.weight(1f)
        ) {
            navigation<ScreenGroup.DiscoverScreensGroup>(
                startDestination = ScreenGroup.DiscoverScreensGroup.getFirstScreenOfGroup()
            ) {
                composable<Screen.Discover> {
                    DiscoverScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.Discover
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.Discover }
                        }
                    }
                }
                composable<Screen.NewsList>(
                    enterTransition = {
                        slideIn(
                            initialOffset = { fullSize ->
                                IntOffset(0, fullSize.height)
                            }
                        )
                    },
                    exitTransition = {
                        slideOut(
                            targetOffset = { fullSize ->
                                IntOffset(0, fullSize.height + fullSize.height / 4)
                            }
                        )
                    }
                ) {
                    NewsListScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.NewsList
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.NewsList }
                        }
                    }
                }
                composable<Screen.StrategiesList>(
                    enterTransition = {
                        if (!openedScreens.any { it is Screen.StrategyDetails })
                            slideIn(
                                initialOffset = { fullSize ->
                                    IntOffset(0, fullSize.height)
                                }
                            )
                        else fadeIn()
                    },
                    exitTransition = {
                        if (!openedScreens.any { it is Screen.StrategyDetails })
                            slideOut(
                                targetOffset = { fullSize ->
                                    IntOffset(0, fullSize.height + fullSize.height / 4)
                                }
                            )
                        else fadeOut()
                    }
                ) {
                    StrategiesListScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.StrategiesList
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.StrategiesList }
                        }
                    }
                }
                composable<Screen.ArticlesList>(
                    enterTransition = {
                        if (!openedScreens.any { it is Screen.ArticleDetails })
                            slideIn(
                                initialOffset = { fullSize ->
                                    IntOffset(0, fullSize.height)
                                }
                            )
                        else fadeIn()
                    },
                    exitTransition = {
                        if (!openedScreens.any { it is Screen.ArticleDetails })
                            slideOut(
                                targetOffset = { fullSize ->
                                    IntOffset(0, fullSize.height + fullSize.height / 4)
                                }
                            )
                        else fadeOut()
                    }
                ) {
                    ArticlesListScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.ArticlesList
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.ArticlesList }
                        }
                    }
                }
                composable<Screen.StrategyDetails>(
                    enterTransition = {
                        slideIn(
                            initialOffset = { fullSize ->
                                IntOffset(0, fullSize.height)
                            }
                        )
                    },
                    exitTransition = {
                        slideOut(
                            targetOffset = { fullSize ->
                                IntOffset(0, fullSize.height + fullSize.height / 4)
                            }
                        )
                    },
                    typeMap = mapOf(
                        typeOf<Screen.StrategyDetails>() to parcelableType<Screen.StrategyDetails>()
                    )
                ) {
                    val route = it.toRoute<Screen.StrategyDetails>()
                    StrategyDetailsScreenRoot(route.strategyId, navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + route
                        onDispose {
                            openedScreens = openedScreens.filterNot { it == route }
                        }
                    }
                }
                composable<Screen.ArticleDetails>(
                    enterTransition = {
                        slideIn(
                            initialOffset = { fullSize ->
                                IntOffset(0, fullSize.height)
                            }
                        )
                    },
                    exitTransition = {
                        slideOut(
                            targetOffset = { fullSize ->
                                IntOffset(0, fullSize.height + fullSize.height / 4)
                            }
                        )
                    },
                    typeMap = mapOf(
                        typeOf<Screen.ArticleDetails>() to parcelableType<Screen.ArticleDetails>()
                    )
                ) {
                    val route = it.toRoute<Screen.ArticleDetails>()
                    ArticleDetailsScreenRoot(route.articleId, navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + route
                        onDispose {
                            openedScreens = openedScreens.filterNot { it == route }
                        }
                    }
                }
            }
            navigation<ScreenGroup.TrainingScreensGroup>(
                startDestination = ScreenGroup.TrainingScreensGroup.getFirstScreenOfGroup()
            ) {
                composable<Screen.Training> {
                    TrainingScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.Training
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.Training }
                        }
                    }
                }
            }
            navigation<ScreenGroup.SignalsScreensGroup>(
                startDestination = ScreenGroup.SignalsScreensGroup.getFirstScreenOfGroup()
            ) {
                composable<Screen.Signals> {
                    SignalsScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.Signals
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.Signals }
                        }
                    }
                }
            }
            navigation<ScreenGroup.ContactUsScreensGroup>(
                startDestination = ScreenGroup.ContactUsScreensGroup.getFirstScreenOfGroup()
            ) {
                composable<Screen.ContactUs> {
                    ContactUsScreenRoot(navController)
                    DisposableEffect(true) {
                        openedScreens = openedScreens + Screen.ContactUs
                        onDispose {
                            openedScreens = openedScreens.filterNot { it is Screen.ContactUs }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = !showNavigationBar) {
            Spacer(modifier = Modifier.height(systemNavBarHeight))
        }
        AnimatedVisibility(visible = showNavigationBar) {
            BottomNavigationBar(
                navigationBarItems = navigationBarItems,
                selectedItemIndex = selectedItemIndex,
                onItemClick = { screen, index ->
                    if (selectedItemIndex != index) {
                        val navOptionsBuilder = navOptionsEachTabIsRootTop(currentTabRootScreen!!)
                        navController.navigate(screen, navOptionsBuilder)
                    } else {
                        val navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
                            popUpTo(screen)
                        }
                        navController.navigate(
                            screen,
                            navOptionsBuilder
                        )
                    }
                },
                modifier = Modifier.wrapContentHeight()
            )
        }
    }
}

@Preview
@Composable
fun AppNavigationPreview(modifier: Modifier = Modifier) {
    TradingOrangeTheme {
        AppNavigation(
            showNavigationBar = true,
            navigationBarItems = BottomNavigationItems.entries,
            selectedItemIndex = 0,
            currentTabRootScreen = null,
            navController = rememberNavController()
        )
    }
}

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)
}

fun NavDestination.getRootRoute(): String? {
    return if (parent?.route == null) {
        this.route
    } else {
        parent!!.getRootRoute() ?: parent?.route
    }
}

/**
 * Use this nav options when navigating to root screen in some of bottom navigation tab.
 * In this case, when you press back button in root screen of any navigation tab, the application wil be closed.
 */
fun navOptionsEachTabIsRootTop(tabRootScreen: Screen): NavOptionsBuilder.() -> Unit {
    val navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
        popUpTo(tabRootScreen) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
    return navOptionsBuilder
}
