package com.githukudenis.summary.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.summary.ui.SummaryRoute

const val summaryNavigationRoute = "summary"

fun NavGraphBuilder.summaryScreen(
    onOpenHabitDetails: (Int) -> Unit
) {
    composable(route = summaryNavigationRoute) {
        SummaryRoute(onOpenHabitDetails = onOpenHabitDetails)
    }
}