package com.githukudenis.summary.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.summary.ui.home.SummaryRoute

const val summaryNavigationRoute = "summary"

fun NavGraphBuilder.summaryScreen(
    onOpenHabitDetails: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    onOpenActivity: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartHabit: (Long) -> Unit
) {
    composable(
        route = summaryNavigationRoute,
        enterTransition = {
            if (targetState.destination.route == "splash_screen") {
                EnterTransition.None
            } else {
                scaleIn(
                    initialScale = 1.2f,
                    animationSpec = tween(300, easing = EaseOut)
                ) + fadeIn()
            }
        },
        exitTransition = {
            scaleOut(
                targetScale = 1.2f,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeOut()
        }
    ) {
        SummaryRoute(
            onOpenHabitDetails = onOpenHabitDetails,
            onNavigateUp = onNavigateUp,
            onOpenActivity = onOpenActivity,
            onOpenSettings = onOpenSettings,
            onStartHabit = onStartHabit
        )
    }
}
