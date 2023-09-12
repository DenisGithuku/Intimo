package com.githukudenis.intimo.splash_screen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.core.designsystem.theme.QwitcherGrypen
import com.githukudenis.intimo.R
import kotlinx.coroutines.delay

const val splashScreenRoute = "splash_screen"
const val SplashWaitMillis = 3000L


fun NavGraphBuilder.splashScreen(onTimeout: () -> Unit) {
    composable(
        route = splashScreenRoute,
        exitTransition = {
            fadeOut()
        }
    ) {
        SplashScreenRoute(waitMillis = SplashWaitMillis, onTimeout = onTimeout)
    }
}

@Composable
fun SplashScreenRoute(
    waitMillis: Long,
    onTimeout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(color=MaterialTheme.colorScheme.background),
    ) {

        val scale = remember {
            Animatable(0f)
        }

        val currentOnTimeout by rememberUpdatedState(onTimeout)

        LaunchedEffect(true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = {
                        OvershootInterpolator(2f).getInterpolation(it)
                    }
                )
            )
            delay(waitMillis)
            currentOnTimeout()
        }

        Image(
            painter = painterResource(id = com.githukudenis.intimo.core.designsystem.R.drawable.intimologo),
            contentDescription = stringResource(R.string.app_logo),
            modifier = Modifier
                .scale(scale = scale.value)
                .align(Alignment.Center)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = QwitcherGrypen,
            fontSize = 48.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}