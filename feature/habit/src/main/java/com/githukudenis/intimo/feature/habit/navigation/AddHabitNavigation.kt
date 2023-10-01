package com.githukudenis.intimo.feature.habit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.feature.habit.add_habit.AddHabitRoute

const val addHabitRoute = "add_habit"

fun NavGraphBuilder.addHabitScreen(onNavigateUp: () -> Unit) {
    composable(route = addHabitRoute, enterTransition = {
        slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) + scaleIn(
            initialScale = 1.2f,
            animationSpec = tween(300, easing = EaseOut)
        )
    }, exitTransition = {
        slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down) + scaleOut(
            targetScale = 1.2f,
            animationSpec = tween(300, easing = EaseOut)
        )
    }) {
        AddHabitRoute(onNavigateUp = onNavigateUp)
    }
}