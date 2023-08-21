package com.githukudenis.intimo

import androidx.compose.material3.SnackbarHostState
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
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    navController: NavHostController = rememberNavController()
): IntimoAppState {
    return remember(
        navController
    ) {
        IntimoAppState(navController, snackbarHostState)
    }
}

@Stable
data class IntimoAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigate(route: String, popUpTo: String? = null, navOptions: NavOptions? = null, extras: Navigator.Extras? = null) {
        navController.navigate(route) {
            launchSingleTop = true
            if (popUpTo != null) {
                popUpTo(popUpTo) {
                    inclusive = true
                }
            }
        }
    }

    fun popBackStack() = navController.popBackStack()
}