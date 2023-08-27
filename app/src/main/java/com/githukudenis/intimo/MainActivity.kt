package com.githukudenis.intimo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.githukudenis.designsystem.theme.IntimoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            IntimoTheme {
                IntimoApp(
                    shouldHideOnBoarding = shouldHideOnBoarding(uiState),
                    onPopupFailed = { finish() })
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
