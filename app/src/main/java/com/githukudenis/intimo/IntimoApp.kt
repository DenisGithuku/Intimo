package com.githukudenis.intimo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.navigation.IntimoNavHost
import com.githukudenis.onboarding.navigation.onBoardingNavigationRoute
import com.githukudenis.summary.navigation.summaryNavigationRoute

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntimoApp(
    shouldHideOnBoarding: Boolean,
    appState: IntimoAppState = rememberIntimoAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val startDestination = if (shouldHideOnBoarding) summaryNavigationRoute else onBoardingNavigationRoute
   Scaffold { paddingValues ->
       Column(
           modifier = Modifier
               .padding(paddingValues)
               .padding(16.dp)
               .fillMaxSize()
       ) {
           IntimoNavHost(
               appState = appState,
               onShowSnackBar = { message, action ->
               snackbarHostState.showSnackbar(
                   message = message,
                   actionLabel = action
               )
           },
               startDestination = startDestination)
       }
   }
}