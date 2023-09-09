package com.githukudenis.onboarding.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.onboarding.ui.OnBoardingRoute


const val onBoardingNavigationRoute = "on_boarding_route"

fun NavGraphBuilder.onBoardingScreen(onFinishedOnBoarding: () -> Unit) {
    composable(
        route = onBoardingNavigationRoute,
        enterTransition = { slideInHorizontally() },
        exitTransition = { slideOutHorizontally() },
    ) {
        OnBoardingRoute(onFinishedOnBoarding = onFinishedOnBoarding)
    }
}