package com.githukudenis.intimo.feature.activity.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.feature.activity.ui.ActivityRoute

const val activityRoute = "activity"

fun NavGraphBuilder.activityScreen() {
    composable(route = activityRoute) {
        ActivityRoute()
    }
}