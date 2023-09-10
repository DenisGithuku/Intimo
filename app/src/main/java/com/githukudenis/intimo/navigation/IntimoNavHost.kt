package com.githukudenis.intimo.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.intimo.feature.activity.navigation.activityScreen
import com.githukudenis.intimo.habit.navigation.activeHabitRoute
import com.githukudenis.intimo.habit.navigation.activeHabitScreen
import com.githukudenis.intimo.habit.navigation.detailScreen
import com.githukudenis.intimo.habit.navigation.habitDetailRoute
import com.githukudenis.intimo.settings.navigation.settingsRoute
import com.githukudenis.intimo.settings.navigation.settingsScreen
import com.githukudenis.intimo.splash_screen.splashScreen
import com.githukudenis.intimo.splash_screen.splashScreenRoute
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.onboarding.navigation.onBoardingScreen
import com.githukudenis.summary.navigation.summaryNavigationRoute
import com.githukudenis.summary.navigation.summaryScreen

@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    startDestination: String,
    onOpenActivity: () -> Unit,
    onPopupFailed: () -> Unit,
) {

    NavHost(navController = appState.navController, startDestination = splashScreenRoute) {
        splashScreen(onTimeout = {
            appState.navigate(startDestination, splashScreenRoute)
        })
        onBoardingScreen(
            onFinishedOnBoarding = {
                appState.navigate(summaryNavigationRoute, onBoardingNavigationRoute)
            }
        )
        summaryScreen(
            onOpenHabitDetails = { habitId ->
                appState.navigate("${habitDetailRoute}/$habitId")
            }, onNavigateUp = {
                if (!appState.navController.popBackStack()) {
                    onPopupFailed()
                }
            }, onOpenActivity = onOpenActivity,
            onOpenSettings = {
                appState.navigate(settingsRoute)
            },
            onStartHabit = { habitId ->
                appState.navigate("${activeHabitRoute}/$habitId")
            }
        )

        detailScreen(
            onNavigateUp = {
                appState.popBackStack()
            }
        )

        activeHabitScreen(onHabitCompleted = {
            if (!appState.navController.popBackStack()) {
                appState.navigate(summaryNavigationRoute)
            }
        }, onNavigateUp = {
            Log.d("dest", appState.navController.currentDestination?.route.toString())
            if (!appState.navController.popBackStack()) {
                appState.navigate(summaryNavigationRoute)
            }
        })

        settingsScreen()

        activityScreen()
    }
}
