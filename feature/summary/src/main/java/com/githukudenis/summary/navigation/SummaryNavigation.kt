package com.githukudenis.summary.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
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
    onOpenActivity: () -> Unit,
    onOpenSettings: () -> Unit
) {
    composable(
        route = summaryNavigationRoute,
        enterTransition = {
            scaleIn(
                initialScale = 1.1f,
                animationSpec = tween(100, easing = LinearEasing))
        }
    ) {
        SummaryRoute(
            snackbarHostState = snackbarHostState,
            onOpenHabitDetails = onOpenHabitDetails,
            onNavigateUp = onNavigateUp,
            onOpenActivity = onOpenActivity,
            onOpenSettings = onOpenSettings
        )
    }
}