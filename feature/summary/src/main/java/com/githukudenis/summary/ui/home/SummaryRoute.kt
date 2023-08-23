package com.githukudenis.summary.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.githukudenis.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.nameToString
import com.githukudenis.summary.ui.MessageType
import com.githukudenis.summary.ui.UserMessage
import com.githukudenis.summary.ui.components.CardInfo
import com.githukudenis.summary.ui.components.HabitCard
import com.githukudenis.summary.ui.components.PersonalizeSheet
import com.githukudenis.summary.ui.components.TimePickerDialog
import com.githukudenis.summary.util.hasNotificationAccessPermissions
import com.githukudenis.summary.util.hasUsageAccessPermissions
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun SummaryRoute(
    snackbarHostState: SnackbarHostState,
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    onOpenHabitDetails: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    onOpenActivity: () -> Unit
) {

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
                            false -> SnackbarDuration.Indefinite
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
                        summaryViewModel.onEvent(SummaryUiEvent.DismissMessage(userMessage.id))
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
                summaryViewModel.onEvent(SummaryUiEvent.DismissMessage(userMessage.id))
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
                        summaryViewModel.onEvent(SummaryUiEvent.DismissMessage(userMessage.id))
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
                summaryViewModel.onEvent(SummaryUiEvent.DismissMessage(userMessage.id))
                onNavigateUp()
            }
        )
    }

    val bottomSheetVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    SummaryScreen(
        usageStats = uiState.summaryData?.usageStats?.appUsageList ?: emptyList(),
        unlockCount = uiState.summaryData?.unlockCount ?: 0,
        notificationCount = uiState.notificationCount,
        habitDataList = uiState.habitDataList,
        onCheckHabit = { habitId ->
            summaryViewModel.onEvent(SummaryUiEvent.CheckHabit(habitId))
        },
        onOpenHabit = { habitId -> onOpenHabitDetails(habitId) },
        usageStatsLoading = uiState.summaryData?.usageStats?.appUsageList?.isEmpty() == true,
        onOpenActivity = onOpenActivity,
        onCustomize = {
            summaryViewModel.onEvent(SummaryUiEvent.EditHabit(it))
            bottomSheetVisible.value = !bottomSheetVisible.value
        }
    )

    if (bottomSheetVisible.value) {
        PersonalizeSheet(onDismiss = { bottomSheetVisible.value = !bottomSheetVisible.value }) {

            var showTimePicker = rememberSaveable {
                mutableStateOf(false)
            }

            val timePickerState = rememberTimePickerState()

            val habitStartTime = remember {
                uiState.habitInEditModeState.habitModel?.startTime?.let {
                    Instant.fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }

            val habitEndTime = remember {
                uiState.habitInEditModeState.habitModel?.let { habitUiModel ->
                    Instant.fromEpochMilliseconds(habitUiModel.startTime + habitUiModel.duration)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }


            val calendar = rememberSaveable {
                mutableStateOf(Calendar.getInstance().apply {
                    isLenient = false
                })
            }

            val dateTimeFormatter = remember {
                SimpleDateFormat(
                    if (timePickerState.is24hour) "HH:mm" else "HH:mm a",
                    Locale.getDefault()
                )
            }
            val formattedStartTime = dateTimeFormatter.format(
                calendar.value.apply {
                    habitStartTime?.hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    habitStartTime?.minute?.let { set(Calendar.MINUTE, it) }
                }.time
            )
            val formattedEndTime = dateTimeFormatter.format(
                calendar.value.apply {
                    habitEndTime?.hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    habitEndTime?.minute?.let { set(Calendar.MINUTE, it) }
                }.time
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                uiState.habitInEditModeState.habitModel?.let { habitUiModel ->
                    Text(
                        text = habitUiModel.habitType.nameToString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AssistChip(onClick = {
                            showTimePicker.value = true
                        }, label = {
                            Text(
                                text = formattedStartTime,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }, trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Change time"
                            )
                        })
                        Text(text = " - ")
                        AssistChip(onClick = {
                            showTimePicker.value = true
                        }, label = {
                            Text(
                                text = formattedEndTime,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }, trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Change time"
                            )
                        })
                    }
                }
                Button(onClick = { summaryViewModel.onEvent(SummaryUiEvent.UpdateHabit) }) {
                    Text(
                        text = "Save"
                    )
                }
            }
            if (showTimePicker.value) {
                TimePickerDialog(
                    onCancel = { showTimePicker.value = false },
                    onConfirm = {
                        calendar.value = calendar.value.apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.hour)
                            isLenient = false
                        }
                        showTimePicker.value = false
                        Log.d("time", calendar.value.time.toString())
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun SummaryScreen(
    usageStatsLoading: Boolean,
    usageStats: List<ApplicationInfoData>,
    habitDataList: List<HabitUiModel>,
    unlockCount: Int,
    notificationCount: Long,
    onCheckHabit: (Long) -> Unit,
    onOpenHabit: (Long) -> Unit,
    onOpenActivity: () -> Unit,
    onCustomize: (Long) -> Unit
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.screen_time),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        appUsageData(
            usageStats = usageStats,
            unlockCount = unlockCount,
            notificationCount = notificationCount,
            context = context,
            isLoading = usageStatsLoading,
            onOpenActivity = onOpenActivity
        )
        item {
            Text(
                text = "Your habits",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        habitList(
            habitDataList = habitDataList,
            onOpenHabit = onOpenHabit,
            onCheckHabit = onCheckHabit,
            onCustomize = onCustomize
        )
    }
}

fun LazyListScope.appUsageData(
    isLoading: Boolean,
    usageStats: List<ApplicationInfoData>,
    unlockCount: Int,
    notificationCount: Long,
    context: Context,
    onOpenActivity: () -> Unit
) {
    item {
        Surface(
            onClick = onOpenActivity,
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Fetching details..."
                            )
                        }
                    }

                    false -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                val totalAppUsage =
                                    usageStats.sumOf { it.usageDuration.toInt() }.toFloat()

                                /*
                                    splice most four used apps
                                     */
                                val fourMostUsedApps =
                                    usageStats.take(4).map { it.usageDuration.toFloat() }
                                        .toMutableList()

                                /*
                                    get sum of remaining values
                                     */
                                val remainingTotalUsage =
                                    usageStats.drop(4).sumOf { it.usageDuration.toInt() }.toFloat()

                                /*
                                    add remaining usage to first four apps
                                     */
                                fourMostUsedApps.add(remainingTotalUsage)

                                /*
                                    Create colors to map to usage duration
                                     */
                                val colors = listOf(
                                    Color.Blue.copy(green = .7f),
                                    Color.Green.copy(),
                                    Color.LightGray,
                                    Color.Black.copy(alpha = .7f),
                                    Color.Yellow.copy(green = .7f)
                                )

                                /*
                                    create a combination of usage duration and color
                                     */
                                val usageWithColors = fourMostUsedApps zip colors

                                /*
                                    values to be plotted on canvas
                                     */
                                val plotValues = usageWithColors.map { usageStats ->
                                    usageStats.first * 100 / totalAppUsage
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
                                val angles = plotValues.map {
                                    it * 360f / 100
                                }

                                val textMeasurer = rememberTextMeasurer()
                                val totalAppTime = usageStats.sumOf { it.usageDuration }
                                val totalAppTimeText = getTimeFromMillis(totalAppTime)


                                val textLayoutResult = remember(totalAppTimeText) {
                                    textMeasurer.measure(totalAppTimeText)
                                }
                                Spacer(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .drawWithCache {
                                            var startAngle = -90f

                                            onDrawBehind {
                                                for (i in angles.indices) {
                                                    drawArc(
                                                        color = usageWithColors.map { it.second }[i],
                                                        startAngle = startAngle * animateArchValue.value,
                                                        sweepAngle = angles[i],
                                                        useCenter = false,
                                                        style = Stroke(width = 16.dp.value),
                                                    )
                                                    startAngle += angles[i]
                                                }
                                                drawText(
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
                                    usageWithColors.forEach { appUsage ->
                                        val appName = usageStats
                                            .find { it.usageDuration.toFloat() == appUsage.first }?.packageName?.let {
                                                getApplicationLabel(
                                                    it,
                                                    context
                                                )
                                            } ?: "Other"
                                        /*
                                        Generate total use time for each
                                        Use the value of other summed apps
                                         */
                                        val upTime =
                                            usageStats.find { it.usageDuration.toFloat() == appUsage.first }?.usageDuration
                                                ?: fourMostUsedApps.last().toLong()
                                        val formattedTime = getTimeFromMillis(upTime)
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Spacer(
                                                modifier = Modifier
                                                    .size(15.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(appUsage.second)
                                            )
                                            Text(
                                                text = buildString {
                                                    append("$appName ")
                                                    append(formattedTime)
                                                },
                                                style = MaterialTheme.typography.labelSmall
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


fun getTimeFromMillis(timeInMillis: Long): String {
    return if (timeInMillis / 1000 / 60 / 60 >= 1) {
        "${timeInMillis / 1000 / 60 / 60}hr ${timeInMillis / 1000 / 60 % 60}min"
    } else if (timeInMillis / 1000 / 60 >= 1) {
        "${timeInMillis / 1000 / 60}min"
    } else if (timeInMillis / 1000 >= 1) {
        "Less than a minute"
    } else {
        "0 min"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.habitList(
    habitDataList: List<HabitUiModel>,
    onCheckHabit: (Long) -> Unit,
    onOpenHabit: (Long) -> Unit,
    onCustomize: (Long) -> Unit
) {
    items(items = habitDataList, key = { it.habitId }) { habitUiModel ->
        HabitCard(
            habitUiModel = habitUiModel,
            onCheckHabit = onCheckHabit,
            onOpenHabitDetails = { habitId ->
                onOpenHabit(habitId)
            },
            onCustomize = onCustomize
        )
    }
}

fun getApplicationLabel(packageName: String, context: Context): String {
    val appInfo =
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return context.packageManager.getApplicationLabel(appInfo).toString()
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SummaryScreenPrev() {
    SummaryScreen(
        usageStats = emptyList(),
        unlockCount = 100,
        notificationCount = 230,
        habitDataList = emptyList(),
        onCheckHabit = {},
        onOpenHabit = {},
        usageStatsLoading = true,
        onOpenActivity = {},
        onCustomize = {}
    )
}

