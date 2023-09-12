package com.githukudenis.intimo.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.intimo.feature.habit.navigation.activeHabitRoute
import com.githukudenis.intimo.feature.habit.navigation.activeHabitScreen
import com.githukudenis.intimo.feature.habit.navigation.detailScreen
import com.githukudenis.intimo.feature.habit.navigation.habitDetailRoute
import com.githukudenis.intimo.feature.settings.navigation.settingsRoute
import com.githukudenis.intimo.feature.settings.navigation.settingsScreen
import com.githukudenis.intimo.splash_screen.splashScreen
import com.githukudenis.intimo.splash_screen.splashScreenRoute
import com.githukudenis.intimo.feature.usage_stats.usageStatsRoute
import com.githukudenis.intimo.feature.usage_stats.usageStatsScreen
import com.githukudenis.intimo.feature.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.intimo.feature.onboarding.navigation.onBoardingScreen
import com.githukudenis.intimo.feature.summary.navigation.summaryNavigationRoute
import com.githukudenis.intimo.feature.summary.navigation.summaryScreen

@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    startDestination: String,
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
                appState.navigate("$habitDetailRoute/$habitId")
            }, onNavigateUp = {
                if (!appState.navController.popBackStack()) {
                    onPopupFailed()
                }
            },
            onOpenSettings = {
                appState.navigate(settingsRoute)
            },
            onStartHabit = { habitId ->
                appState.navigate("$activeHabitRoute/$habitId")
            },
            onOpenUsageStats = {
                appState.navigate(usageStatsRoute)
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

        usageStatsScreen( onNavigateUp = { appState.navController.navigateUp() } )

        settingsScreen()
    }
}
