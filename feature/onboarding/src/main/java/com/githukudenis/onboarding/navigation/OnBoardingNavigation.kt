package com.githukudenis.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.githukudenis.onboarding.ui.OnBoardingRoute


const val onBoardingNavigationRoute = "on_boarding_route"

fun NavGraphBuilder.onBoardingScreen(onFinishedOnBoarding: () -> Unit) {
    composable(route = onBoardingNavigationRoute) {
        OnBoardingRoute(onFinishedOnBoarding = onFinishedOnBoarding)
    }
}