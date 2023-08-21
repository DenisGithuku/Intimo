package com.githukudenis.intimo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SsidChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.feature.activity.navigation.activityRoute
import com.githukudenis.intimo.navigation.IntimoNavHost
import com.githukudenis.intimo.settings.navigation.settingsRoute
import com.githukudenis.intimo.splash_screen.splashScreenRoute
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.summary.navigation.summaryNavigationRoute
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntimoApp(
    shouldHideOnBoarding: Boolean,
    onPopupFailed: () -> Unit,
    appState: IntimoAppState = rememberIntimoAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val startDestination =
        if (shouldHideOnBoarding) summaryNavigationRoute else onBoardingNavigationRoute
    val currentDestination = appState.currentDestination?.route
    val bottomNavItems = listOf(
        BottomNavigationItem(
            title = "Home",
            route = summaryNavigationRoute,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            badgeCount = null
        ),
        BottomNavigationItem(
            title = "Activity",
            route = activityRoute,
            selectedIcon = Icons.Filled.SsidChart,
            unselectedIcon = Icons.Outlined.SsidChart,
            hasNews = false,
            badgeCount = null
        ),
        BottomNavigationItem(
            title = "Settings",
            route = settingsRoute,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            badgeCount = null
        ),
    )

    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            if (currentDestination != null && currentDestination != onBoardingNavigationRoute && currentDestination != splashScreenRoute) {
                MediumTopAppBar(
                    navigationIcon = {
                        if (currentDestination != summaryNavigationRoute) {
                            IconButton(onClick = {
                                appState.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.navigate_back)
                                )
                            }
                        }
                    },
                    title = {
                        Text(
                            text = if (currentDestination == summaryNavigationRoute) getCurrentDate() else
                                currentDestination.substringBefore("/")
                                    .replaceFirstChar { it.uppercase() }
                        )
                    },
                    scrollBehavior = scrollBehaviour,

                    )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                enter = fadeIn() + slideInVertically(initialOffsetY = { 5 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 5 }),
                visible = currentDestination != null &&
                        currentDestination != splashScreenRoute &&
                        currentDestination != onBoardingNavigationRoute && !currentDestination.startsWith(
                    "detail"
                )

            ) {
                NavigationBar {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            label = {
                                Text(
                                    text = item.title
                                )
                            },
                            selected = item.route == currentDestination,
                            onClick = {
                                appState.navigate(item.route)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (item.route == currentDestination) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            IntimoNavHost(
                appState = appState,
                onShowSnackBar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action
                    )
                },
                startDestination = startDestination,
                onPopupFailed = onPopupFailed,
                onOpenActivity = {
                    appState.navigate(activityRoute)
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentDate(): String {
    val today = LocalDate.now()
    return today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
}


data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null
)
