package com.githukudenis.intimo.feature.usage_stats

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.ui.components.IntimoActionDialog
import com.githukudenis.intimo.core.ui.components.IntimoAlertDialog
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.usage_stats.components.UsageChart
import com.githukudenis.intimo.feature.usage_stats.components.UsageStatsCard
import com.githukudenis.intimo.feature.usage_stats.services.AppLaunchService
import com.githukudenis.intimo.feature.usage_stats.services.AppsUsageRefreshService
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun UsageStatsRoute(
    viewModel: UsageStatsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

//    val lifecycle = LocalLifecycleOwner.current.lifecycle
//
//    var shouldShowOverlayPermissionsDialog by rememberSaveable {
//        mutableStateOf(false)
//    }

//    val overlayPermissionsLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult(),
//        onResult = {
//            if (!context.hasWindowOverlayPermission()) {
//                onNavigateUp()
//            }
//        })

//    DisposableEffect(lifecycle, shouldShowOverlayPermissionsDialog) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> {
//                    shouldShowOverlayPermissionsDialog = !context.hasWindowOverlayPermission()
//                }
//
//                else -> Unit
//            }
//        }
//        lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycle.removeObserver(observer)
//        }
//    }

//    if (shouldShowOverlayPermissionsDialog) {
//        AlertDialog(onDismissRequest = {
//            shouldShowOverlayPermissionsDialog = false
//            onNavigateUp()
//        },
//            title = {
//                Text(
//                    text = stringResource(id = R.string.overlay_permission_dialog_title),
//                )
//            },
//            text = {
//                Text(
//                    text = stringResource(id = R.string.overlay_permission_dialog_text),
//                )
//            },
//            dismissButton = {
//                TextButton(onClick = {
//                    if (!context.hasWindowOverlayPermission()) {
//                        onNavigateUp()
//                    }
//                }) {
//                    Text(
//                        text = stringResource(id = R.string.dialog_dismiss_button)
//                    )
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).run {
//                        data = Uri.parse("package:" + context.packageName)
//                        overlayPermissionsLauncher.launch(this)
//                    }
//                }) {
//                    Text(
//                        text = stringResource(id = R.string.dialog_accept_button)
//                    )
//                }
//            }
//        )
//    }

    val activityManager: ActivityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    UsageStatsScreen(
        uiState = uiState,
        onRetry = {
            viewModel.onRetry()
        },
        onNavigateUp = onNavigateUp,
        onSetAppLimit = { packageName, limitDuration ->
            viewModel.onEvent(
                UsageStatsUiEvent.LimitApp(packageName, limitDuration)
            )
            context.startService(Intent(context, AppsUsageRefreshService::class.java))

            //check if app launch service is started and if not start it
            if (!activityManager.getRunningServices(Integer.MAX_VALUE).any { service ->
                    service.service.className == AppLaunchService::class.java.name
                }) {
                // start app launch monitor service
                Intent(context, AppLaunchService::class.java).run {
                    context.startService(this)
                }
            }
        },
        onShowMessage = { messageId ->
            viewModel.onEvent(
                UsageStatsUiEvent.DismissUserMessage(messageId)
            )
        },
        onChangeDateListener = { date ->
            viewModel.onEvent(UsageStatsUiEvent.ChangeDate(date))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UsageStatsScreen(
    uiState: UsageStatsUiState,
    onRetry: () -> Unit,
    onNavigateUp: () -> Unit,
    onSetAppLimit: (String, Long) -> Unit,
    onShowMessage: (Long) -> Unit,
    onChangeDateListener: (LocalDate) -> Unit,
) {
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
                title = {
                    Text(
                        text = "Usage"
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            UsageStatsUiState.Loading -> {
                LoadingScreen(
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        end = 16.dp,
                        start = 16.dp
                    ),
                    modifier = Modifier.consumeWindowInsets(innerPadding)
                )
            }

            is UsageStatsUiState.Loaded -> LoadedScreen(
                modifier = Modifier.consumeWindowInsets(innerPadding),
                userMessages = uiState.userMessages,
                dataUsageStats = uiState.usageStats,
                chartState = uiState.chartState,
                appUsageLimits = uiState.appsInFocusMode,
                onSetAppLimit = onSetAppLimit,
                onShowMessage = onShowMessage,
                innerPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    end = 16.dp,
                    start = 16.dp
                ),
                snackbarHostState = snackbarHostState,
                onChangeDateListener = onChangeDateListener,
            )

            is UsageStatsUiState.Error -> ErrorScreen(
                uiState.userMessageList.first(),
                onRetry = onRetry
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadedScreen(
    modifier: Modifier = Modifier,
    userMessages: List<UserMessage>,
    dataUsageStats: DataUsageStats,
    chartState: ChartState,
    appUsageLimits: List<AppInFocusMode>,
    onSetAppLimit: (String, Long) -> Unit,
    onShowMessage: (Long) -> Unit,
    innerPadding: PaddingValues = PaddingValues(16.dp),
    snackbarHostState: SnackbarHostState,
    onChangeDateListener: (LocalDate) -> Unit
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
        modifier = modifier
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
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
            modifier = Modifier.animateContentSize()
        ) {
            item {
                UsageChart(
                    chartState = chartState,
                    onChangeDateListener = onChangeDateListener,
                )
            }
            items(
                dataUsageStats.appUsageList,
                key = { it.packageName }) { applicationInfoData ->

                if (dataUsageStats.appUsageList.isEmpty()) {
                    Row(
                        modifier = Modifier.padding(32.dp, 16.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.empty_app_usage_stats),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    val usageLimit =
                        appUsageLimits.find { it.packageName == applicationInfoData.packageName }?.limitDuration
                            ?: 0L
                    UsageStatsCard(
                        modifier = Modifier.animateItemPlacement(),
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
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
            ) {
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


@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    val colors = remember {
        listOf(
            Color.LightGray.copy(alpha = 0.4f),
            Color.LightGray.copy(alpha = 0.1f),
            Color.LightGray.copy(alpha = 0.4f),
        )
    }

    val infiniteTransition =
        rememberInfiniteTransition(label = "infinite transition loading skeleton")
    val transitionAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                delayMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "loading skeleton"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = transitionAnimation.value, y = transitionAnimation.value)
    )
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .matchParentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp).fillMaxWidth(0.5f).clip(RoundedCornerShape(32.dp)).background(brush))
                    Row(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.6f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.5f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.2f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.4f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.2f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.8f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.45f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(0.6f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(brush)
                    )
                }
            }
        }
        items(count = 20) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush = brush)
                )
                Column {
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush = brush)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth(0.3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush = brush)
                    )
                }
            }
        }
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

@Preview
@Composable
fun LoadingScreenPrev() {
    LoadingScreen()
}