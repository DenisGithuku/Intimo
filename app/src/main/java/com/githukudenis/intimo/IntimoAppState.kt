package com.githukudenis.intimo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberIntimoAppState(
    navController: NavHostController = rememberNavController()
): IntimoAppState {
    return remember(
        navController
    ) {
        IntimoAppState(navController)
    }
}

@Stable
data class IntimoAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigate(route: String, navOptions: NavOptions? = null, extras: Navigator.Extras? = null) {
        navController.navigate(route) {
            popUpTo(route)
        }
    }
}