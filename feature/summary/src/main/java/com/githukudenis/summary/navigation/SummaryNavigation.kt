package com.githukudenis.summary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.summary.ui.SummaryRoute

const val summaryNavigationRoute = "summary"

fun NavGraphBuilder.summaryScreen() {
    composable(route = summaryNavigationRoute) {
         SummaryRoute()
    }
}