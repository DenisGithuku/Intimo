package com.githukudenis.summary.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val habitDetailRoute = "detail"

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailScreen() {
    composable(
        "$habitDetailRoute/{habitId}",
        arguments = listOf(
            navArgument("habitId") {
                type = NavType.LongType
            }
        ),
        enterTransition = {
            scaleIn() + fadeIn()
        },
        exitTransition = {
            scaleOut() + fadeOut()
        }
    ) {
        HabitDetailRoute()
    }
}