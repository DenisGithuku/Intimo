package com.githukudenis.intimo.splash_screen

import android.content.res.Configuration
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.githukudenis.intimo.R
import kotlinx.coroutines.delay

const val splashScreenRoute = "splash_screen"

fun NavGraphBuilder.splashScreen(onTimeout: () -> Unit) {
    composable(route = splashScreenRoute) {
        SplashScreenRoute(onTimeout = onTimeout)
    }
}

@Composable
fun SplashScreenRoute(
    onTimeout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val scale = remember {
            Animatable(0f)
        }

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
            delay(3000)
            onTimeout()
        }

        Image(
            painter = painterResource(id = R.drawable.intimologo),
            contentDescription = stringResource(R.string.app_logo),
            modifier = Modifier.scale(scale = scale.value)
        )
    }
}

@Preview(
    device = "spec:width=1440px,height=3120px,dpi=560",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true, showBackground = true
)
@Composable
fun SplashScreenRoutePreview() {
    SplashScreenRoute {

    }
}