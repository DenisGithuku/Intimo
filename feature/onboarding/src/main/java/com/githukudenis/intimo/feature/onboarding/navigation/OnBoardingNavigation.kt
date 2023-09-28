package com.githukudenis.intimo.feature.onboarding.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.feature.onboarding.ui.habit_selection.OnBoardingRoute
import com.githukudenis.intimo.feature.onboarding.ui.pager.PagerRoute


const val onBoardingNavigationRoute = "on_boarding_route"
const val pagerRoute = "pager_route"

fun NavGraphBuilder.onBoardingScreen(onFinishedOnBoarding: () -> Unit) {
    composable(
        route = onBoardingNavigationRoute,
        enterTransition = {
            scaleIn(
                initialScale = 1.2f, animationSpec = tween(300, easing = EaseOut)
            ) + fadeIn()
        },
        exitTransition = {
            scaleOut(
                targetScale = 1.2f, animationSpec = tween(300, easing = EaseOut)
            ) + fadeOut()
        },
    ) {
        OnBoardingRoute(onFinishedOnBoarding = onFinishedOnBoarding)
    }
}

fun NavGraphBuilder.pagerScreen(
    onGetStarted: () -> Unit
) {
    composable(
        route = pagerRoute,
        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) },
//        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) },
    ) {
        PagerRoute(onGetStarted = onGetStarted)
    }
}