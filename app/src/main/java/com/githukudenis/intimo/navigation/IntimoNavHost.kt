package com.githukudenis.intimo.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.intimo.feature.activity.navigation.activityScreen
import com.githukudenis.intimo.settings.navigation.settingsRoute
import com.githukudenis.intimo.settings.navigation.settingsScreen
import com.githukudenis.intimo.splash_screen.splashScreen
import com.githukudenis.intimo.splash_screen.splashScreenRoute
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.onboarding.navigation.onBoardingScreen
import com.githukudenis.summary.navigation.summaryNavigationRoute
import com.githukudenis.summary.navigation.summaryScreen
import com.githukudenis.intimo.habit.navigation.detailScreen
import com.githukudenis.intimo.habit.navigation.habitDetailRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    startDestination: String,
    onOpenActivity: () -> Unit,
    onPopupFailed: () -> Unit,
) {
    val navController = appState.navController

    NavHost(navController = navController, startDestination = splashScreenRoute) {
        splashScreen(onTimeout = {
            appState.navigate(startDestination, splashScreenRoute)
        })
        onBoardingScreen(
            onFinishedOnBoarding = {
                appState.navigate(summaryNavigationRoute, onBoardingNavigationRoute)
            }
        )
        summaryScreen(
            snackbarHostState = appState.snackbarHostState,
            onOpenHabitDetails = { habitId ->
                appState.navigate("${com.githukudenis.intimo.habit.navigation.habitDetailRoute}/$habitId")
            }, onNavigateUp = {
                if (!navController.popBackStack()) {
                    onPopupFailed()
                }
            }, onOpenActivity = onOpenActivity,
            onOpenSettings = {
                appState.navigate(settingsRoute)
            }
        )

        detailScreen()

        settingsScreen()

        activityScreen()
    }
}
