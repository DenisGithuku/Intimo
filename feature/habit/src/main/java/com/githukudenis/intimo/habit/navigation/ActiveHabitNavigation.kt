package com.githukudenis.intimo.habit.navigation

import android.content.Intent
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.githukudenis.intimo.habit.active.ActiveHabitRoute

const val activeHabitRoute = "active_habit"

fun NavGraphBuilder.activeHabitScreen(
    onHabitCompleted: () -> Unit,
    onNavigateUp: () -> Unit
) {
    composable(
        route = "$activeHabitRoute/{habitId}",
        arguments = listOf(
            navArgument("habitId") {
                type = NavType.LongType
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "intimo://active_habit/{habitId}"
                action = Intent.ACTION_VIEW
            }
        ),
        enterTransition = {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeIn()
        },
        exitTransition = {
            fadeOut()
        }
    ) {
        ActiveHabitRoute(onHabitCompleted = onHabitCompleted, onNavigateUp = onNavigateUp)
    }
}