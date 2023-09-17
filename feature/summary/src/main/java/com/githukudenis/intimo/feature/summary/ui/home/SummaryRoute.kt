package com.githukudenis.intimo.feature.summary.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.MessageType
import com.githukudenis.intimo.core.util.TimeFormatter
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.intimo.feature.summary.ui.components.CardInfo
import com.githukudenis.intimo.feature.summary.ui.components.HabitCard
import com.githukudenis.intimo.feature.summary.ui.components.HabitHistoryComponent
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance
import com.githukudenis.intimo.feature.summary.ui.components.NotificationCard
import com.githukudenis.intimo.feature.summary.util.hasNotificationAccessPermissions
import com.githukudenis.intimo.feature.summary.util.hasUsageAccessPermissions
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SummaryRoute(
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    onOpenHabitDetails: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartHabit: (Long) -> Unit,
    onOpenUsageStats: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = getTimeStatus(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        onClick = onOpenSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(
                                id = R.string.settings
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->

        val context = LocalContext.current
        val uiState by summaryViewModel.uiState.collectAsStateWithLifecycle()


        var shouldShowUsagePermissionsDialog by rememberSaveable {
            mutableStateOf(false)
        }

        var shouldShowNotificationPermissionsDialog by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(uiState.userMessageList, snackbarHostState) {
            if (uiState.userMessageList.isNotEmpty()) {
                val userMessage = uiState.userMessageList.first()
                snackbarHostState.showSnackbar(
                    message = userMessage.message ?: "An error occurred",
                    duration = when (val messageType = userMessage.messageType) {
                        MessageType.INFO -> {
                            SnackbarDuration.Short
                        }

                        is MessageType.ERROR -> {
                            when (messageType.dismissable) {
                                true -> SnackbarDuration.Short
                                false -> SnackbarDuration.Long
                            }
                        }
                    },
                )
                summaryViewModel.onEvent(SummaryUiEvent.DismissMessage(userMessage.id))
            }
        }

        val usageAccessPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (shouldShowNotificationPermissionsDialog || shouldShowNotificationPermissionsDialog) {
                    return@rememberLauncherForActivityResult
                }
                if (context.hasUsageAccessPermissions()) {
                    summaryViewModel.onEvent(SummaryUiEvent.Refresh)
                } else {
                    val userMessage = UserMessage(
                        message = "Usage access permissions required",
                        messageType = MessageType.ERROR(dismissable = false)
                    )
                    summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                }
            }
        )

        val usagePermissionsAllowed = context.hasUsageAccessPermissions()
        val notificationAccessPermissionsAllowed = context.hasNotificationAccessPermissions()

        val lifecycle = LocalLifecycleOwner.current.lifecycle

        DisposableEffect(lifecycle, usagePermissionsAllowed, notificationAccessPermissionsAllowed) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        shouldShowUsagePermissionsDialog =
                            !context.hasUsageAccessPermissions() && shouldShowNotificationPermissionsDialog == false
                        shouldShowNotificationPermissionsDialog =
                            !context.hasNotificationAccessPermissions()
                    }

                    else -> Unit
                }
            }

            lifecycle.addObserver(observer)

            onDispose {
                lifecycle.removeObserver(observer)
            }
        }

        if (shouldShowUsagePermissionsDialog) {
            AlertDialog(
                properties = DialogProperties(
                    dismissOnBackPress = false, dismissOnClickOutside = false
                ),
                title = {
                    Text(
                        text = context.getString(R.string.usage_permission_dialog_title)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            usageAccessPermissionLauncher.launch(intent)
                        }
                    ) {
                        Text(
                            text = context.getString(R.string.permission_dialog_positive_button)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            shouldShowUsagePermissionsDialog = false
                            val userMessage = UserMessage(
                                message = "Usage access permissions required",
                                messageType = MessageType.ERROR(dismissable = false)
                            )
                            summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                            onNavigateUp()
                        }
                    ) {
                        Text(
                            text = context.getString(R.string.permission_dialog_negative_button)
                        )
                    }
                },
                text = {
                    Text(
                        text = context.getString(R.string.usage_permission_dialog_description)
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = LocalTonalElevation.current.large,
                onDismissRequest = {
                    shouldShowUsagePermissionsDialog = false
                    val userMessage = UserMessage(
                        message = "Usage access permissions required",
                        messageType = MessageType.ERROR(dismissable = false)
                    )
                    summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                    onNavigateUp()
                }
            )
        }
        if (shouldShowNotificationPermissionsDialog) {
            AlertDialog(
                properties = DialogProperties(
                    dismissOnBackPress = false, dismissOnClickOutside = false
                ),
                title = {
                    Text(
                        text = context.getString(R.string.notification_access_permission_dialog_title)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent =
                                Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                            usageAccessPermissionLauncher.launch(intent)
                        }
                    ) {
                        Text(
                            text = context.getString(R.string.permission_dialog_positive_button)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            shouldShowNotificationPermissionsDialog = false
                            val userMessage = UserMessage(
                                message = "Notification access permissions required",
                                messageType = MessageType.ERROR(dismissable = false)
                            )
                            summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                            onNavigateUp()
                        }
                    ) {
                        Text(
                            text = context.getString(R.string.permission_dialog_negative_button)
                        )
                    }
                },
                text = {
                    Text(
                        text = context.getString(R.string.notification_access_permission_dialog_description)
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = LocalTonalElevation.current.large,
                onDismissRequest = {
                    shouldShowNotificationPermissionsDialog = false
                    val userMessage = UserMessage(
                        message = "Notification access permissions required",
                        messageType = MessageType.ERROR(dismissable = false)
                    )
                    summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                    onNavigateUp()
                }
            )
        }

        Crossfade(
            targetState = uiState.isLoading,
            label = "Screen animation"
        ) {
            when (it) {
                true -> {
                    LoadingScreen(
                        modifier = Modifier.consumeWindowInsets(paddingValues),
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
                    )
                }

                false -> {
                    SummaryScreen(
                        modifier = Modifier
                            .consumeWindowInsets(paddingValues),
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        ),
                        runningHabitState = uiState.runningHabitState,
                        usageStats = uiState.summaryData?.usageStats?.appUsageList ?: emptyList(),
                        unlockCount = uiState.summaryData?.unlockCount ?: 0,
                        notificationCount = uiState.notificationCount,
                        habitDataList = uiState.habitDataList,
                        habitPerformance = uiState.habitPerformance,
                        onOpenHabit = { habitId -> onOpenHabitDetails(habitId) },
                        usageStatsLoading = uiState.summaryData?.usageStats?.appUsageList?.isEmpty() == true,
                        onStart = { habitId ->
                            if (uiState.runningHabitState.habitId != null && uiState.runningHabitState.habitId != habitId) {
                                summaryViewModel.onEvent(
                                    SummaryUiEvent.ShowMessage(
                                        UserMessage(
                                            id = 1,
                                            message = context.getString(R.string.multiple_habit_running_error_text),
                                            messageType = MessageType.ERROR(dismissable = true)
                                        )
                                    )
                                )
                                return@SummaryScreen
                            }
                            onStartHabit(habitId)
                        },
                        onOpenUsageStats = onOpenUsageStats,
                        habitProgress = uiState.habitHistoryStateList,
                        onSelectDayOnHistory = { date ->
                            summaryViewModel.onEvent(SummaryUiEvent.SelectDayOnHistory(date))
                        }
                    )
                }
            }
        }
    }
}


@Composable
internal fun SummaryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    usageStatsLoading: Boolean,
    runningHabitState: RunningHabitState,
    usageStats: List<ApplicationInfoData>,
    habitDataList: List<HabitUiModel>,
    habitPerformance: HabitPerformance,
    habitProgress: Map<Date, Float>,
    onSelectDayOnHistory: (Date) -> Unit,
    unlockCount: Int,
    notificationCount: Int,
    onOpenHabit: (Long) -> Unit,
    onStart: (Long) -> Unit,
    onOpenUsageStats: () -> Unit
) {

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        item {
            Text(
                text = stringResource(R.string.screen_time),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                )
            )
        }
        appUsageData(
            appUsageStats = usageStats,
            unlockCount = unlockCount,
            notificationCount = notificationCount,
            context = context,
            isLoading = usageStatsLoading,
            onOpenUsageStats = onOpenUsageStats
        )
        item {
            Text(
                text = stringResource(R.string.habit_history_title),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                )
            )
        }
        item {
            HabitHistoryComponent(
                habitProgress = habitProgress,
                onSelectDay = onSelectDayOnHistory
            )
        }
        item {
            NotificationCard(
                habitPerformance = habitPerformance,
                onTakeAction = {
                    scope.launch {
                        listState.animateScrollToItem(7)
                    }
                }
            )
        }
        item {
            Text(
                text = stringResource(R.string.active_habits_section_title),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                )
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                habitList(
                    habitDataList = habitDataList,
                    onOpenHabit = onOpenHabit,
                    runningHabitState = runningHabitState,
                    onStart = onStart
                )
            }
        }

    }
}

fun LazyListScope.appUsageData(
    isLoading: Boolean,
    appUsageStats: List<ApplicationInfoData>,
    unlockCount: Int,
    notificationCount: Int,
    context: Context,
    onOpenUsageStats: () -> Unit
) {
    item {
        Surface(
            onClick = onOpenUsageStats,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.1f
                )
            )
        ) {
            Crossfade(
                targetState = isLoading, label = "usage_stats",
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) {
                when (it) {
                    true -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Fetching details..."
                                )
                            }
                        }
                    }

                    false -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                /*
                                    Get total app usage
                                     */
                                val totalAppUsage = remember(appUsageStats) {
                                    appUsageStats.sumOf { usageStat -> usageStat.usageDuration.toInt() }
                                        .toFloat()
                                }

                                /*
                                    splice most four used apps
                                     */
                                val fourMostUsedAppDurations =
                                    appUsageStats.take(4).map { app -> app.usageDuration.toFloat() }
                                        .toMutableList()


                                /*
                                    get sum of remaining values
                                     */
                                val remainingTotalUsage =
                                    appUsageStats.drop(4)
                                        .sumOf { usage -> usage.usageDuration.toInt() }.toFloat()


                                /*
                                    add remaining usage to first four apps
                                     */
                                fourMostUsedAppDurations.add(remainingTotalUsage)

                                /*
                                    values to be plotted on canvas
                                     */
                                val plotValues =
                                    fourMostUsedAppDurations.map { duration ->
                                        duration * 100 / totalAppUsage
                                    }


                                val animateArchValue = remember {
                                    Animatable(0f)
                                }

                                LaunchedEffect(key1 = plotValues) {
                                    animateArchValue.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            durationMillis = 1000,
                                            easing = EaseOut
                                        )
                                    )
                                }

                                /*
                                    derive plot angles
                                     */
                                val angles =
                                    plotValues.map { value ->
                                        value * 360f / 100
                                    }


                                val textMeasurer = rememberTextMeasurer()
                                val totalAppTime =
                                    appUsageStats.sumOf { appUsage -> appUsage.usageDuration }
                                val totalAppTimeText = TimeFormatter.getTimeFromMillis(totalAppTime)


                                val textLayoutResult = remember(totalAppTimeText) {
                                    textMeasurer.measure(totalAppTimeText)
                                }
                                val labelMedium = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Spacer(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .drawWithCache {
                                            var startAngle = -90f

                                            onDrawBehind {
                                                for (i in angles.indices) {
                                                    /*
                                                        Retrieve color generated from icon or use secondary app color
                                                        */
                                                    val arcColor =
                                                        fourMostUsedAppDurations.map { duration ->
                                                            appUsageStats.find { usageStat -> usageStat.usageDuration.toFloat() == duration }
                                                        }[i]?.colorSwatch ?: (0xFF3A5BAB).toInt()

                                                    drawArc(
                                                        color = Color(arcColor),
                                                        startAngle = startAngle * animateArchValue.value,
                                                        sweepAngle = angles[i],
                                                        useCenter = false,
                                                        style = Stroke(width = 12.dp.toPx()),
                                                    )
                                                    startAngle += angles[i]
                                                }

                                                drawText(
                                                    style = labelMedium,
                                                    textMeasurer = textMeasurer,
                                                    text = totalAppTimeText,
                                                    topLeft = Offset(
                                                        x = center.x - textLayoutResult.size.width / 2,
                                                        y = center.y - textLayoutResult.size.height / 2
                                                    )
                                                )
                                            }
                                        })
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    fourMostUsedAppDurations.forEach { usage ->
                                        val appName =
                                            appUsageStats
                                                .find { app -> app.usageDuration.toFloat() == usage }?.packageName?.let { packageName ->
                                                    getApplicationLabel(
                                                        packageName,
                                                        context
                                                    )
                                                } ?: "Other"

                                        /*
                                        Generate total use time for each
                                        Use the value of other summed apps
                                         */
                                        val upTime =
                                            appUsageStats.find { app -> app.usageDuration.toFloat() == usage }?.usageDuration
                                                ?: fourMostUsedAppDurations.last().toLong()

                                        val formattedTime = TimeFormatter.getTimeFromMillis(upTime)


                                        /*
                                        Retrieve color generated from icon or use secondary app color
                                         */
                                        val color =
                                            appUsageStats.find { app -> app.usageDuration.toFloat() == usage }?.colorSwatch
                                                ?: (0xFF3A5BAB).toInt()


                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(
                                                modifier = Modifier
                                                    .size(15.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        Color(
                                                            color
                                                        )
                                                    )
                                            )
                                            Text(
                                                text = buildString {
                                                    append("$appName ")
                                                    append(formattedTime)
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.8f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            Divider(
                                modifier = Modifier
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                CardInfo(
                                    title = "Unlocks",
                                    value = "$unlockCount"
                                )
                                CardInfo(
                                    title = "Notifications",
                                    value = "$notificationCount"
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
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
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth(0.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
        item {
            UsageStatsShimmerCard(brush = brush)
        }
        item {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth(0.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
        item {
            HistorySectionShimmer(brush = brush)
        }
        item {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth(0.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
        item {
            Box(
                modifier = Modifier
                    .border(
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(
                            width = 1.dp,
                            brush = brush
                        )
                    )
                    .padding(12.dp)
                    .fillMaxWidth(),
            ) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .height(12.dp)
                                    .fillMaxWidth(0.8f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(12.dp)
                                    .fillMaxWidth(0.8f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(12.dp)
                                    .fillMaxWidth(0.3f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(brush = brush)
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp).fillMaxWidth(0.2f).clip(RoundedCornerShape(12.dp)).background(brush = brush))
                }
            }
        }
        item {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth(0.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(count = 12) {
                    LoadingShimmerListView(brush = brush)
                }
            }
        }

    }
}

@Composable
fun UsageStatsShimmerCard(brush: Brush) {
    Box(
        modifier = Modifier
            .border(brush = brush, shape = MaterialTheme.shapes.medium, width = 1.dp)
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(
                            brush = brush,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 12.dp.toPx()
                            )
                        )
                    }
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth(0.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                        )

                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    (0..4).forEach { _ ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth()
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(brush)
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(10.dp)
                                    .fillMaxWidth(0.6f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush)
                            )
                        }
                    }
                }
            }
            Divider(thickness = 1.dp, color = Color.LightGray.copy(0.4f))

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(30.dp, 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                    Spacer(
                        modifier = Modifier
                            .size(80.dp, 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(30.dp, 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                    Spacer(
                        modifier = Modifier
                            .size(80.dp, 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Composable
fun HistorySectionShimmer(
    brush: Brush
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(count = 10) {
            Canvas(modifier = Modifier.size(40.dp)) {
                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun LoadingShimmerListView(brush: Brush) {
    Column(
        modifier = Modifier
            .border(brush = brush, shape = MaterialTheme.shapes.medium, width = 1.dp)
            .padding(12.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

        }
        Spacer(
            modifier = Modifier
                .size(100.dp, 20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush)
        )
        Spacer(
            modifier = Modifier
                .size(120.dp, 15.dp)
                .clip(MaterialTheme.shapes.large)
                .background(brush)
        )
        Spacer(
            modifier = Modifier
                .size(150.dp, 10.dp)
                .clip(MaterialTheme.shapes.large)
                .background(brush)
        )
        Spacer(
            modifier = Modifier
                .size(100.dp, 40.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(brush)
        )
    }
}


fun LazyListScope.habitList(
    runningHabitState: RunningHabitState,
    habitDataList: List<HabitUiModel>,
    onOpenHabit: (Long) -> Unit,
    onStart: (Long) -> Unit
) {
    items(items = habitDataList, key = { it.habitId }) { habitUiModel ->
        HabitCard(
            habitUiModel = habitUiModel,
            isRunning = runningHabitState.habitId == habitUiModel.habitId,
            onOpenHabitDetails = { habitId ->
                onOpenHabit(habitId)
            }, onStart = onStart
        )
    }
}

fun getApplicationLabel(packageName: String, context: Context): String {
    val appInfo =
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return context.packageManager.getApplicationLabel(appInfo).toString()
}

private fun getTimeStatus(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour > 15 -> {
            "Good evening"
        }

        hour >= 12 -> {
            "Good afternoon"
        }

        else -> {
            "Good morning"
        }
    }
}

@Preview
@Composable
fun SummaryScreenPrev() {
    SummaryScreen(
        contentPadding = PaddingValues(12.dp),
        usageStatsLoading = false,
        runningHabitState = RunningHabitState(
            habitId = 2, isRunning = true, remainingTime = 25000L
        ),
        usageStats = listOf(),
        habitDataList = listOf(
            HabitUiModel(
                Pair(0L, false),
                10000L,
                2,
                "\uD83E\uDEC1",
                HabitType.BREATHING,
                startTime = 1627350000L,
                duration = 30000L,
                durationType = DurationType.MINUTE
            ),
            HabitUiModel(
                Pair(0L, true),
                1000L,
                3,
                "\uD83C\uDFC3",
                HabitType.EXERCISE,
                startTime = 1627350000L,
                duration = 30000L,
                durationType = DurationType.MINUTE
            ),
            HabitUiModel(
                Pair(0L, false),
                1000L,
                4,
                "\uD83D\uDCDA",
                HabitType.READING,
                startTime = 1627350000L,
                duration = 30000L,
                durationType = DurationType.MINUTE
            ),
        ),
        unlockCount = 2,
        notificationCount = 9,
        onOpenHabit = {},
        onStart = {},
        onOpenUsageStats = {},
        habitProgress = mapOf(
            Date(LocalDate.now().minusDays(2)) to 0.5f,
            Date(LocalDate.now().minusDays(1)) to 0.6f,
            Date(LocalDate.now()) to 0.8f,
        ),
        onSelectDayOnHistory = {},
        habitPerformance = HabitPerformance.EXCELLENT
    )
}


@Preview
@Composable
fun LoadingScreenPrev() {
    LoadingScreen()
}