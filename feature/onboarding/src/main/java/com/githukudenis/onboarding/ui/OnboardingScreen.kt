package com.githukudenis.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.githukudenis.onboarding.R
import com.githukudenis.onboarding.ui.components.PageContent
import kotlinx.coroutines.delay


@Composable
fun OnBoardingScreen(
    onFinishedOnBoarding: () -> Unit
) {
    OnBoardingContent { onFinishedOnBoarding }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnBoardingContent(
    onNext: () -> Unit
) {
    val context = LocalContext.current

    val pageInfoList = listOf(
        PageInfo(
            image = R.drawable.hands_holding_phone,
            imageDescription = context.getString(R.string.woman_holding_phone),
            title = context.getString(R.string.reminder_title),
            description = context.getString(R.string.onboarding_reminder_text)
        ),
        PageInfo(
            image = R.drawable.usage_stats,
            imageDescription = context.getString(R.string.usage_stats_image_description),
            title = context.getString(R.string.usage_stats),
            description = context.getString(R.string.usage_stats_description)
        ),
        PageInfo(
            image = R.drawable.journaling,
            imageDescription = context.getString(R.string.journaling_image_description),
            title = context.getString(R.string.journaling_title),
            description = context.getString(R.string.journaling_description)
        )
    )

    val pagerState = rememberPagerState(initialPage = 0)

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        if (pagerState.currentPage < pageInfoList.size) {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            pageCount = pageInfoList.size,
            state = pagerState,
        ) { index ->
            PageContent(pageInfoList[index])
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageInfoList.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (index == pagerState.currentPage) Color.DarkGray else Color.LightGray)
                        .size(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible = pagerState.currentPage == pageInfoList.size - 1) {
            Button(modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                onClick = {}) {
                Text(
                    text = context.getString(R.string.get_started_button),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

data class PageInfo(
    val image: Int,
    val imageDescription: String,
    val title: String,
    val description: String
)

