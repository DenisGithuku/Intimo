package com.githukudenis.summary.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.summary.ui.SummaryRoute

const val summaryNavigationRoute = "summary"

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.summaryScreen(
    onOpenHabitDetails: (Long) -> Unit
) {
    composable(route = summaryNavigationRoute) {
        SummaryRoute(onOpenHabitDetails = onOpenHabitDetails)
    }
}