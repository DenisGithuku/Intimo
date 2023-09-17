package com.githukudenis.intimo.feature.settings.navigation

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.feature.settings.ui.SettingsRoute

const val settingsRoute = "settings"

fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
    onOpenLicenses: () -> Unit
) {
    composable(route = settingsRoute,
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
        SettingsRoute(
            onNavigateUp = onNavigateUp,
            onOpenLicenses = onOpenLicenses
        )
    }
}