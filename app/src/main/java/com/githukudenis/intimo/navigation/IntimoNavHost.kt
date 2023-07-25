package com.githukudenis.intimo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.githukudenis.intimo.IntimoAppState
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.onboarding.navigation.onBoardingScreen
import com.githukudenis.summary.navigation.summaryNavigationRoute
import com.githukudenis.summary.navigation.summaryScreen

@Composable
fun IntimoNavHost(
    appState: IntimoAppState,
    onShowSnackBar: suspend (String, String?) -> Unit,
    startDestination: String
) {
    val navController = appState.navController

    NavHost(navController = navController, startDestination = startDestination) {
//        onBoardingScreen(
//            onFinishedOnBoarding = {
//                navController.navigate(summaryNavigationRoute) {
//                    popUpTo(onBoardingNavigationRoute){
//                        inclusive = true
//                    }
//                }
//            }
//        )
        summaryScreen()
    }
}