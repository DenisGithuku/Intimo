package com.githukudenis.summary.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.summary.ui.home.SummaryRoute

const val summaryNavigationRoute = "summary"

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.summaryScreen(
    snackbarHostState: SnackbarHostState,
    onOpenHabitDetails: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    onOpenActivity: () -> Unit
) {
    composable(route = summaryNavigationRoute) {
        SummaryRoute(
            snackbarHostState = snackbarHostState,
            onOpenHabitDetails = onOpenHabitDetails,
            onNavigateUp = onNavigateUp,
            onOpenActivity = onOpenActivity
        )
    }
}