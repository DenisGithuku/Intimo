package com.githukudenis.intimo.feature.summary.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.core.util.MessageType
import com.githukudenis.intimo.core.util.TimeFormatter
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.feature.summary.ui.components.CardInfo
import com.githukudenis.intimo.feature.summary.ui.components.HabitCard
import com.githukudenis.intimo.feature.summary.util.hasNotificationAccessPermissions
import com.githukudenis.intimo.feature.summary.util.hasUsageAccessPermissions
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
            MediumTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
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
                        shouldShowUsagePermissionsDialog = !context.hasUsageAccessPermissions()
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

        AnimatedContent(
            targetState = uiState.isLoading,
            label = "Screen animation"
        ) {
            when (it) {
                true -> {
                    LoadingScreen()
                }

                false -> {
                    SummaryScreen(
                        modifier = Modifier
                            .consumeWindowInsets(paddingValues),
                        contentPadding = paddingValues,
                        runningHabitState = uiState.runningHabitState,
                        usageStats = uiState.summaryData?.usageStats?.appUsageList ?: emptyList(),
                        unlockCount = uiState.summaryData?.unlockCount ?: 0,
                        notificationCount = uiState.notificationCount,
                        habitDataList = uiState.habitDataList,
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
                        onOpenUsageStats = onOpenUsageStats
                    )
                }
            }
        }
    }
}


@Composable
internal fun SummaryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    usageStatsLoading: Boolean,
    runningHabitState: RunningHabitState,
    usageStats: List<ApplicationInfoData>,
    habitDataList: List<HabitUiModel>,
    unlockCount: Int,
    notificationCount: Int,
    onOpenHabit: (Long) -> Unit,
    onStart: (Long) -> Unit,
    onOpenUsageStats: () -> Unit
) {

    val context = LocalContext.current

    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
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
                modifier = Modifier.padding(horizontal = 12.dp),
                text = "Your habits",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                )
            )
        }
        habitList(
            habitDataList = habitDataList,
            onOpenHabit = onOpenHabit,
            runningHabitState = runningHabitState,
            onStart = onStart
        )
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
            modifier = Modifier.padding(horizontal = 12.dp),
            onClick = onOpenUsageStats,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(
                width = 1.dp,
                color = Color.Black.copy(
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
                            contentAlignment = Center
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
                                verticalAlignment = CenterVertically,
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
                                                        style = Stroke(width = 16.dp.value),
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
                                            verticalAlignment = CenterVertically
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
                                    .height(1.dp)
                                    .background(color = Color.Black.copy(alpha = 0.1f))
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
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = "Fetching data...",
                style = MaterialTheme.typography.labelMedium
            )
        }
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


