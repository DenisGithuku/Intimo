package com.githukudenis.intimo.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.intimo.feature.activity.navigation.activityScreen
import com.githukudenis.intimo.settings.navigation.settingsScreen
import com.githukudenis.intimo.splash_screen.splashScreen
import com.githukudenis.intimo.splash_screen.splashScreenRoute
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.onboarding.navigation.onBoardingScreen
import com.githukudenis.summary.navigation.summaryNavigationRoute
import com.githukudenis.summary.navigation.summaryScreen
import com.githukudenis.summary.ui.detail.detailScreen
import com.githukudenis.summary.ui.detail.habitDetailRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    onShowSnackBar: suspend (String, String?) -> Unit,
    startDestination: String
) {
    val navController = appState.navController

    NavHost(navController = navController, startDestination = splashScreenRoute) {
        splashScreen(onTimeout = {
            navController.navigate(startDestination) {
                popUpTo(splashScreenRoute) {
                    inclusive = true
                }
            }
        })
        onBoardingScreen(
            onFinishedOnBoarding = {
                navController.navigate(summaryNavigationRoute) {
                    popUpTo(onBoardingNavigationRoute){
                        inclusive = true
                    }
                }
            }
        )
        summaryScreen(onOpenHabitDetails = { habitId ->
            navController.navigate("$habitDetailRoute/$habitId")
        })

        detailScreen()

        settingsScreen()

        activityScreen()
    }
}
