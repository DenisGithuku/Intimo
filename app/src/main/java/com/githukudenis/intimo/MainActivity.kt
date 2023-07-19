package com.githukudenis.intimo

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.githukudenis.designsystem.theme.IntimoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = LocalView.current.context as Activity
            val backgroundArgb = MaterialTheme.colorScheme.background.toArgb()
            activity.window.statusBarColor = backgroundArgb

            val wic = WindowCompat.getInsetsController(window, window.decorView)
            wic.isAppearanceLightStatusBars = !isSystemInDarkTheme()

            IntimoTheme {
                IntimoApp()
            }
        }
    }
}
