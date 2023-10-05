package com.githukudenis.intimo.feature.onboarding.ui.pager

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.feature.onboarding.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PagerRoute(
    onGetStarted: () -> Unit,
) {
    val context = LocalContext.current

    val pageItems = remember {
        listOf(
            Pair(R.drawable.phone_use, context.getString(R.string.phone_use)),
            Pair(R.drawable.mindful, context.getString(R.string.mindfulness)),
            Pair(R.drawable.habit_tracker, context.getString(R.string.habit_tracking)),
        )
    }


    val pagerState = rememberPagerState(pageCount = { pageItems.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(
                top = PaddingValues().calculateTopPadding(),
                start = 16.dp,
                end = 16.dp,
                bottom = PaddingValues().calculateBottomPadding()
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pageItems.size - 1)
                }
            }
        ) {
            Text(
                text = "Skip"
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.heightIn(min = 500.dp)
        ) { page ->
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.requiredHeight(500.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(id = pageItems[page].first),
                    contentDescription = pageItems[page].second,
                    modifier = Modifier
                        .heightIn(386.dp)
                        .widthIn(328.dp)
                )
                Text(
                    text = pageItems[page].second,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pageItems.forEachIndexed { index, _ ->
                val scale =
                    animateFloatAsState(targetValue = if (index == pagerState.currentPage) 1.2f else 1f)
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        }
                        .clip(CircleShape)
                        .background(
                            animateColorAsState(
                                targetValue = if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.1f
                                )
                            ).value
                        )
                        .graphicsLayer {
                            scaleX = if (index == pagerState.currentPage) 1.2f else 1f
                            scaleY = if (index == pagerState.currentPage) 1.2f else 1f
                        }
                )
                if (index < pageItems.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickableOnce {
                        if (pagerState.currentPage > pagerState.initialPage) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                    .border(
                        border = if (pagerState.currentPage > pagerState.initialPage) {
                            BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        } else {
                            BorderStroke(
                                width = 0.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        shape = CircleShape
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Prev",
                    tint = if (pagerState.currentPage > pagerState.initialPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                )
            }
            AnimatedContent(targetState = pagerState.currentPage == pageItems.size - 1) { state ->
                when (state) {
                    true -> {
                        Button(onClick = onGetStarted) {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    false -> {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .animateContentSize()
                                .clickableOnce {
                                    if (pagerState.currentPage < pageItems.size - 1) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pagerState.currentPage + 1
                                            )
                                        }
                                    } else {
                                        onGetStarted()
                                    }
                                }
                        ) {

                            Icon(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp),
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Prev",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                }
            }


        }
    }
}

@Preview
@Composable
fun PagerRoutePrev() {
    PagerRoute {

    }
}