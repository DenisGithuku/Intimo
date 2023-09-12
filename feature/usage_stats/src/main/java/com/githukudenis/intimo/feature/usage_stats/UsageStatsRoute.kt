package com.githukudenis.intimo.feature.usage_stats

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.ui.components.IntimoActionDialog
import com.githukudenis.intimo.core.ui.components.IntimoAlertDialog
import com.githukudenis.intimo.core.util.UserMessage
import kotlinx.coroutines.launch

@Composable
fun UsageStatsRoute(
    viewModel: UsageStatsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UsageStatsScreen(
        usageStatsUiState = uiState,
        onRetry = {
            viewModel.onRetry()
        },
        onNavigateUp = onNavigateUp,
        onSetAppLimit = { packageName, limitDuration ->
            viewModel.onEvent(
                UsageStatsUiEvent.LimitApp(packageName, limitDuration)
            )
        },
        onShowMessage = { messageId ->
            viewModel.onEvent(
                UsageStatsUiEvent.DismissUserMessage(messageId)
            )
        })
}

@Composable
internal fun UsageStatsScreen(
    usageStatsUiState: UsageStatsUiState,
    onRetry: () -> Unit,
    onNavigateUp: () -> Unit,
    onSetAppLimit: (String, Long) -> Unit,
    onShowMessage: (Long) -> Unit
) {
    when (usageStatsUiState) {
        UsageStatsUiState.Loading -> {
            LoadingScreen()
        }

        is UsageStatsUiState.Loaded -> LoadedScreen(
            dataUsageStats = usageStatsUiState.usageStats,
            appUsageLimits = usageStatsUiState.appsInFocusMode,
            onNavigateUp = onNavigateUp,
            onSetAppLimit = onSetAppLimit,
            userMessages = usageStatsUiState.userMessages,
            onShowMessage = onShowMessage
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
    userMessages: List<UserMessage>,
    dataUsageStats: DataUsageStats,
    appUsageLimits: List<AppInFocusMode>,
    onNavigateUp: () -> Unit,
    onSetAppLimit: (String, Long) -> Unit,
    onShowMessage: (Long) -> Unit
) {
    val appLimitDialogIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    var hourlyLimit by rememberSaveable {
        mutableLongStateOf(0L)
    }

    var minutelyLimit by rememberSaveable {
        mutableLongStateOf(0L)
    }

    val infoDialogIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val selectedApp = remember {
        mutableStateOf(dataUsageStats.appUsageList.first())
    }

    val limitTimeChanged = rememberSaveable {
        mutableStateOf(false)
    }

    if (appLimitDialogIsVisible.value) {
        IntimoActionDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.limit_time_text),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
            },
            description = {
                Text(
                    text = stringResource(id = R.string.limit_time_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            },
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            if (hourlyLimit.toInt() == 24) {
                                return@IconButton
                            }
                            hourlyLimit++
                            limitTimeChanged.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropUp,
                                contentDescription = stringResource(id = R.string.increase_time_icon_text),
                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.8f
                                )
                            )
                        }
                        Divider(
                            thickness = 1.dp,
                            color = DividerDefaults.color.copy(
                                alpha = 0.8f
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = String.format("%02d hrs", hourlyLimit),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Divider(
                            thickness = 1.dp,
                            color = DividerDefaults.color.copy(
                                alpha = 0.8f
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        IconButton(onClick = {
                            if (hourlyLimit.toInt() == 0) {
                                return@IconButton
                            }
                            hourlyLimit--
                            limitTimeChanged.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(id = R.string.decrease_time_icon_text),
                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.8f
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            if (minutelyLimit.toInt() == 59) {
                                minutelyLimit = 0
                            }
                            minutelyLimit++
                            limitTimeChanged.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropUp,
                                contentDescription = stringResource(id = R.string.increase_time_icon_text)
                            )
                        }
                        Divider(
                            thickness = 1.dp,
                            color = DividerDefaults.color.copy(
                                alpha = 0.8f
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = String.format("%02d mins", minutelyLimit),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Divider(
                            thickness = 1.dp,
                            color = DividerDefaults.color.copy(
                                alpha = 0.8f
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        IconButton(onClick = {
                            if (minutelyLimit.toInt() == 0) {
                                return@IconButton
                            }
                            minutelyLimit--
                            limitTimeChanged.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(id = R.string.decrease_time_icon_text)
                            )
                        }
                    }
                }
            },
            onDismissRequest = {
                if (limitTimeChanged.value) {
                        val duration: Long =
                            hourlyLimit * 60 * 60 * 1000L + minutelyLimit * 60 * 1000L
                        onSetAppLimit(selectedApp.value.packageName, duration)

                }
                hourlyLimit = 0L
                minutelyLimit = 0L
                limitTimeChanged.value = false
                appLimitDialogIsVisible.value = false
            }
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

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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

        LaunchedEffect(snackbarHostState, userMessages) {
            if (userMessages.isNotEmpty()) {
                val userMessage = userMessages.first()
                snackbarHostState.showSnackbar(
                    message = userMessage.message ?: "",
                    duration = SnackbarDuration.Short
                )
                onShowMessage(userMessage.id)
            }
        }

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
                    val usageLimit =
                        appUsageLimits.find { it.packageName == applicationInfoData.packageName }?.limitDuration
                            ?: 0L
                    UsageStatsCard(
                        applicationInfoData = applicationInfoData,
                        usageLimit = usageLimit,
                        onOpenLimitDialog = { isSystem ->
                            if (isSystem) {
                                infoDialogIsVisible.value = true
                            } else {
                                selectedApp.value = applicationInfoData
                                if (usageLimit > 0) {
                                    hourlyLimit = usageLimit / 1000 / 60 / 60
                                    minutelyLimit = usageLimit / 1000 / 60 % 60
                                }
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