package com.githukudenis.summary.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val habitDetailRoute = "detail"

fun NavGraphBuilder.detailScreen() {
    composable(
        "$habitDetailRoute/{habitId}",
        arguments = listOf(
            navArgument("habitId") {
                type = NavType.LongType
            }
        ),
        enterTransition = {
            scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(100, easing = LinearEasing)
            )
        }
    ) {
        HabitDetailRoute()
    }
}