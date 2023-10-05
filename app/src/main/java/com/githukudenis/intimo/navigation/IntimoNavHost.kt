package com.githukudenis.intimo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.intimo.feature.habit.navigation.activeHabitRoute
import com.githukudenis.intimo.feature.habit.navigation.activeHabitScreen
import com.githukudenis.intimo.feature.habit.navigation.detailScreen
import com.githukudenis.intimo.feature.habit.navigation.habitDetailRoute
import com.githukudenis.intimo.feature.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.intimo.feature.onboarding.navigation.onBoardingScreen
import com.githukudenis.intimo.feature.onboarding.navigation.pagerRoute
import com.githukudenis.intimo.feature.onboarding.navigation.pagerScreen
import com.githukudenis.intimo.feature.settings.navigation.settingsRoute
import com.githukudenis.intimo.feature.settings.navigation.settingsScreen
import com.githukudenis.intimo.feature.summary.navigation.summaryNavigationRoute
import com.githukudenis.intimo.feature.summary.navigation.summaryScreen
import com.githukudenis.intimo.feature.usage_stats.usageStatsRoute
import com.githukudenis.intimo.feature.usage_stats.usageStatsScreen
import com.githukudenis.intimo.licenses.licensesRoute
import com.githukudenis.intimo.licenses.licensesScreen
import com.githukudenis.intimo.splash_screen.splashScreen
import com.githukudenis.intimo.splash_screen.splashScreenRoute

@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    startDestination: String,
    onPopupFailed: () -> Unit,
    onRequestInAppReview: () -> Unit,
) {

    NavHost(navController = appState.navController, startDestination = splashScreenRoute) {
        splashScreen(onTimeout = {
            appState.navigate(startDestination, splashScreenRoute)
        })
        pagerScreen(
            onGetStarted = {
                appState.navigate(onBoardingNavigationRoute, pagerRoute)
            }
        )
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
            onStartHabit = { habitId ->
                appState.navigate("$activeHabitRoute/$habitId")
            },
            onNavigateUp = {
                appState.popBackStack()
            }
        )

        activeHabitScreen(onHabitCompleted = {
            if (!appState.navController.popBackStack()) {
                appState.navigate(summaryNavigationRoute)
            }
        }, onNavigateUp = {
            if (!appState.navController.popBackStack()) {
                appState.navigate(summaryNavigationRoute)
            }
        })

        usageStatsScreen(onNavigateUp = { appState.navController.navigateUp() })

        settingsScreen(
            onNavigateUp = {
                appState.popBackStack()
            },
            onOpenLicenses = {
                appState.navigate(licensesRoute)
            },
            onRequestInAppReview = onRequestInAppReview
        )
        licensesScreen(onNavigateUp = {
            appState.popBackStack()
        })
    }
}
