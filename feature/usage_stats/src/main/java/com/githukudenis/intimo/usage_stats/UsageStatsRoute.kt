package com.githukudenis.intimo.usage_stats

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.ui.components.IntimoActionDialog
import com.githukudenis.intimo.core.ui.components.IntimoAlertDialog
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.launch

@Composable
fun UsageStatsRoute(
    viewModel: UsageStatsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UsageStatsScreen(usageStatsUiState = uiState, onRetry = {
        viewModel.onRetry()
    }, onNavigateUp = onNavigateUp)
}

@Composable
internal fun UsageStatsScreen(
    usageStatsUiState: UsageStatsUiState,
    onRetry: () -> Unit,
    onNavigateUp: () -> Unit
) {
    when (usageStatsUiState) {
        UsageStatsUiState.Loading -> {
            LoadingScreen()
        }

        is UsageStatsUiState.Loaded -> LoadedScreen(
            dataUsageStats = usageStatsUiState.usageStats,
            onNavigateUp = onNavigateUp
        )

        is UsageStatsUiState.Error -> ErrorScreen(
            usageStatsUiState.userMessageList.first(),
            onRetry = onRetry
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadedScreen(
    dataUsageStats: DataUsageStats,
    onNavigateUp: () -> Unit
) {
    val appLimitDialogIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val infoDialogIsVisible = rememberSaveable {
        mutableStateOf(false)
    }


    if (appLimitDialogIsVisible.value) {
        IntimoActionDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.limit_time_text),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            },
            description = {
                Text(
                    text = stringResource(id = R.string.limit_time_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            content = {
                Text(
                    text = "Coming right up...",
                    textAlign = TextAlign.Center

                )
            },
            onDismissRequest = { appLimitDialogIsVisible.value = false }
        )
    }



    if (infoDialogIsVisible.value) {
        IntimoAlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(id = R.string.info_icon_text),

                    )
            },
            title = {
                Text(
                    text = stringResource(id = R.string.system_app_info_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center

                )
            }, content = {
                Text(
                    text = stringResource(id = R.string.system_app_info_text),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center

                )
            }, onDismissRequest = {
                infoDialogIsVisible.value = false
            })

    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        text = "Usage"
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val listState = rememberLazyListState()
            val scrollButtonIsVisible = remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0
                }
            }
            val scope = rememberCoroutineScope()
            LazyColumn(
                state = listState,
            ) {
                items(
                    dataUsageStats.appUsageList,
                    key = { it.packageName }) { applicationInfoData ->
                    UsageStatsCard(
                        applicationInfoData = applicationInfoData,
                        onOpenLimitDialog = { isSystem ->
                            if (isSystem) {
                                infoDialogIsVisible.value = true
                            } else {
                                appLimitDialogIsVisible.value = true
                            }
                        }
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = 8.dp
                    ),
                visible = scrollButtonIsVisible.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = {
                    10
                }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 10 })
            ) {
                FilledTonalButton(onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }) {
                    Text(
                        text = stringResource(R.string.back_to_top_button_text)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = stringResource(id = R.string.scroll_back_up_icon_text)
                    )
                }
            }

        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Loading stats.."
        )
    }
}

@Composable
fun ErrorScreen(userMessage: UserMessage, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = userMessage.message ?: "An error occurred",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onRetry) {
            Text(
                text = stringResource(id = R.string.retry_button_text)
            )
        }
    }
}

fun getApplicationLabel(packageName: String, context: Context): String {
    val appInfo =
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return context.packageManager.getApplicationLabel(appInfo).toString()
}