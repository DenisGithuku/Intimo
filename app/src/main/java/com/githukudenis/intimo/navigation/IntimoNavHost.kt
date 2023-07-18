package com.githukudenis.intimo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.onboarding.navigation.onBoardingScreen

@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    onShowSnackBar: suspend (String, String?) -> Unit,
    startDestination: String = onBoardingNavigationRoute
) {
    val navController = appState.navController

    NavHost(navController = navController, startDestination = startDestination) {
        onBoardingScreen(
            onFinishedOnBoarding = {}
        )
    }
}