package com.githukudenis.intimo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.githukudenis.intimo.navigation.IntimoNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntimoApp(
    appState: IntimoAppState = rememberIntimoAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
   Scaffold { padding ->
       Column(
           modifier = Modifier.fillMaxSize()
       ) {
           IntimoNavHost(appState = appState, onShowSnackBar = { message, action ->
               snackbarHostState.showSnackbar(
                   message = message,
                   actionLabel = action
               )
           })
       }
   }
}