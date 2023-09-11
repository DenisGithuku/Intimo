package com.githukudenis.intimo.usage_stats

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val usageStatsRoute = "usage_stats"

fun NavGraphBuilder.usageStatsScreen(onNavigateUp: () -> Unit) {
    composable(
        route = usageStatsRoute,
        enterTransition = { fadeIn() + slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { fadeOut() + slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) }
    ) {
        UsageStatsRoute(onNavigateUp = onNavigateUp)
    }
}