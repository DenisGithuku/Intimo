package com.githukudenis.intimo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.githukudenis.intimo.feature.activity.navigation.activityRoute
import com.githukudenis.intimo.navigation.IntimoNavHost
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.summary.navigation.summaryNavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntimoApp(
    shouldHideOnBoarding: Boolean,
    onPopupFailed: () -> Unit,
    appState: IntimoAppState = rememberIntimoAppState()
) {
    val startDestination =
        if (shouldHideOnBoarding) summaryNavigationRoute else onBoardingNavigationRoute

    IntimoNavHost(
        appState = appState,
        startDestination = startDestination,
        onPopupFailed = onPopupFailed,
        onOpenActivity = {
            appState.navigate(activityRoute)
        }
    )

}


data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null
)
