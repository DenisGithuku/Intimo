package com.githukudenis.intimo

import androidx.compose.runtime.Composable
import com.githukudenis.intimo.navigation.IntimoNavHost
import com.githukudenis.intimo.feature.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.intimo.feature.summary.navigation.summaryNavigationRoute

@Composable
fun IntimoApp(
    shouldHideOnBoarding: Boolean,
    onPopupFailed: () -> Unit,
    appState: IntimoAppState = rememberIntimoAppState()
) {
    val startDestination =
        if (shouldHideOnBoarding) summaryNavigationRoute else onBoardingNavigationRoute

    IntimoNavHost(
        appState = appState,
        startDestination = startDestination,
        onPopupFailed = onPopupFailed,
    )

}
