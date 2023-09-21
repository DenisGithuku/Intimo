package com.githukudenis.intimo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.githukudenis.intimo.core.designsystem.theme.IntimoTheme
import com.githukudenis.intimo.core.model.Theme
import com.githukudenis.intimo.util.InAppReviewProvider
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
            IntimoTheme(
                useDarkTheme = systemInDarkTheme(uiState = uiState)
            ) {
                setSystemTheme(uiState = uiState)
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars =
                    !systemInDarkTheme(uiState)

                IntimoApp(
                    shouldHideOnBoarding = shouldHideOnBoarding(uiState),
                    onPopupFailed = { finish() },
                    onRequestInAppReview = {
                        val inAppReviewProvider = InAppReviewProvider(this)
                        lifecycleScope.launch {
                            val reviewInfo = inAppReviewProvider.requestReviewInfo()
                            reviewInfo?.let {
                                inAppReviewProvider.launchReviewFlow(
                                    this@MainActivity,
                                    reviewInfo
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private fun shouldHideOnBoarding(uiState: MainActivityUiState): Boolean {
        return when (uiState) {
            MainActivityUiState.Loading -> false
            is MainActivityUiState.Success -> uiState.userData.shouldHideOnBoarding
        }
    }

    private fun systemInDarkTheme(uiState: MainActivityUiState): Boolean {
        return when (uiState) {
            MainActivityUiState.Loading -> false
            is MainActivityUiState.Success -> uiState.userData.theme == Theme.DARK
        }
    }

    private fun setSystemTheme(uiState: MainActivityUiState) {
        when (uiState) {
            MainActivityUiState.Loading -> Unit
            is MainActivityUiState.Success -> {
                if (uiState.userData.theme == Theme.SYSTEM) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }
}
