package com.githukudenis.intimo

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.githukudenis.datastore.PreferenceKeys.shouldHideOnBoarding
import com.githukudenis.designsystem.theme.IntimoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)


        // update uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.uiState
                    .onEach {
                        uiState = it
                    }
                    .collect()
            }
        }


        setContent {
            val activity = LocalView.current.context as Activity
            val backgroundArgb = MaterialTheme.colorScheme.background.toArgb()
            activity.window.statusBarColor = backgroundArgb

            val wic = WindowCompat.getInsetsController(window, window.decorView)
            wic.isAppearanceLightStatusBars = !isSystemInDarkTheme()


            IntimoTheme {
                IntimoApp(shouldHideOnBoarding = shouldHideOnBoarding(uiState))
            }
        }
    }

    private fun shouldHideOnBoarding(uiState: MainActivityUiState): Boolean {
        return when (uiState) {
            MainActivityUiState.Loading -> false
            is MainActivityUiState.Success -> uiState.userData.shouldHideOnBoarding
        }
    }
}
