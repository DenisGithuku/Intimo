package com.githukudenis.intimo.feature.summary.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.ui.components.MultipleClicksCutter
import com.githukudenis.intimo.core.ui.components.TimePickerDialog
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.core.ui.components.get
import com.githukudenis.intimo.core.util.MessageType
import com.githukudenis.intimo.core.util.TimeFormatter
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.habit.components.HabitDurationDialog
import com.githukudenis.intimo.feature.habit.detail.formatDurationMillis
import com.githukudenis.intimo.feature.habit.detail.getDaysInAWeek
import com.githukudenis.intimo.feature.habit.detail.getTimeFromMillis
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.intimo.feature.summary.ui.components.CardInfo
import com.githukudenis.intimo.feature.summary.ui.components.HabitCard
import com.githukudenis.intimo.feature.summary.ui.components.HabitHistoryComponent
import com.githukudenis.intimo.feature.summary.ui.components.NotificationCard
import com.githukudenis.intimo.feature.summary.ui.components.SummaryBottomSheet
import com.githukudenis.intimo.feature.summary.util.hasNotificationAccessPermissions
import com.githukudenis.intimo.feature.summary.util.hasUsageAccessPermissions
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Keep
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

    val multipleClicksCutter = remember {
        MultipleClicksCutter.get()
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ), title = {
                Text(
                    text = getTimeStatus(), style = MaterialTheme.typography.headlineSmall
                )
            }, actions = {
                IconButton(onClick = { multipleClicksCutter.processEvent(onOpenSettings) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(
                            id = R.string.settings
                        )
                    )
                }
            }, scrollBehavior = scrollBehavior
            )
        }) { paddingValues ->

        val context = LocalContext.current

        val state by summaryViewModel.uiState.collectAsStateWithLifecycle()

        val scope = rememberCoroutineScope()

        LaunchedEffect(state.userMessageList, snackbarHostState) {
            if (state.userMessageList.isNotEmpty()) {
                val userMessage = state.userMessageList.first()
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

        val usageAccessPermissionLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    if (context.hasUsageAccessPermissions()) {
                        summaryViewModel.onEvent(
                            SummaryUiEvent.PermissionChange(
                                PermissionState(
                                    usagePermissionsAllowed = true
                                )
                            )
                        )
                        summaryViewModel.onEvent(SummaryUiEvent.Refresh)
                    } else {
                        scope.launch {
                            val userMessage = UserMessage(
                                message = context.getString(R.string.usage_access_permissions_message),
                                messageType = MessageType.ERROR(dismissable = false)
                            )
                            summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                        }
                    }
                })

        val permissionListenerPermissionLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    if (context.hasNotificationAccessPermissions()) {
                        summaryViewModel.onEvent(
                            SummaryUiEvent.PermissionChange(
                                PermissionState(
                                    notificationsPermissionsAllowed = true
                                )
                            )
                        )
                        summaryViewModel.onEvent(SummaryUiEvent.Refresh)
                    } else {
                        scope.launch {
                            val userMessage = UserMessage(
                                message = context.getString(R.string.notification_access_permissions_message),
                                messageType = MessageType.ERROR(dismissable = false)
                            )
                            summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                        }
                    }
                })

        val usagePermissionsAllowed by rememberUpdatedState(newValue = context.hasUsageAccessPermissions())
        val notificationAccessPermissionsAllowed by rememberUpdatedState(newValue = context.hasNotificationAccessPermissions())

        Log.d(
            "perms",
            "usage: $usagePermissionsAllowed, notifs $notificationAccessPermissionsAllowed"
        )

        val lifecycle = LocalLifecycleOwner.current.lifecycle

        DisposableEffect(lifecycle, usagePermissionsAllowed, notificationAccessPermissionsAllowed) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        summaryViewModel.onEvent(
                            SummaryUiEvent.PermissionChange(
                                PermissionState(
                                    usagePermissionsAllowed = usagePermissionsAllowed,
                                    notificationsPermissionsAllowed = notificationAccessPermissionsAllowed
                                )
                            )
                        )
                    }

                    else -> Unit
                }
            }

            lifecycle.addObserver(observer)

            onDispose {
                lifecycle.removeObserver(observer)
            }
        }

        if (!usagePermissionsAllowed) {
            AlertDialog(properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            ),
                title = {
                    Text(
                        text = context.getString(R.string.usage_permission_dialog_title)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        usageAccessPermissionLauncher.launch(intent)
                    }) {
                        Text(
                            text = context.getString(R.string.setting_permission_dialog_positive_button)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        summaryViewModel.onEvent(
                            SummaryUiEvent.PermissionChange(
                                PermissionState(
                                    usagePermissionsAllowed = false
                                )
                            )
                        )
                        val userMessage = UserMessage(
                            message = "Usage access permissions required",
                            messageType = MessageType.ERROR(dismissable = false)
                        )
                        summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                        onNavigateUp()
                    }) {
                        Text(
                            text = context.getString(R.string.setting_permission_dialog_negative_button)
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
                    summaryViewModel.onEvent(
                        SummaryUiEvent.PermissionChange(
                            PermissionState(
                                usagePermissionsAllowed = false
                            )
                        )
                    )
                    val userMessage = UserMessage(
                        message = "Usage access permissions required",
                        messageType = MessageType.ERROR(dismissable = false)
                    )
                    summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                    onNavigateUp()
                })
        }
        if (!notificationAccessPermissionsAllowed) {
            AlertDialog(properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            ),
                title = {
                    Text(
                        text = context.getString(R.string.notification_access_permission_dialog_title)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val intent =
                            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                        permissionListenerPermissionLauncher.launch(intent)
                    }) {
                        Text(
                            text = context.getString(R.string.setting_permission_dialog_positive_button)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        summaryViewModel.onEvent(
                            SummaryUiEvent.PermissionChange(
                                PermissionState(
                                    notificationsPermissionsAllowed = false
                                )
                            )
                        )

                        val userMessage = UserMessage(
                            message = "Notification access permissions required",
                            messageType = MessageType.ERROR(dismissable = false)
                        )
                        summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                        onNavigateUp()
                    }) {
                        Text(
                            text = context.getString(R.string.setting_permission_dialog_negative_button)
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
                    summaryViewModel.onEvent(
                        SummaryUiEvent.PermissionChange(
                            PermissionState(
                                notificationsPermissionsAllowed = false
                            )
                        )
                    )

                    val userMessage = UserMessage(
                        message = "Notification access permissions required",
                        messageType = MessageType.ERROR(dismissable = false)
                    )
                    summaryViewModel.onEvent(SummaryUiEvent.ShowMessage(userMessage))
                    onNavigateUp()
                })
        }

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
            initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000, delayMillis = 500, easing = FastOutSlowInEasing
                ), repeatMode = RepeatMode.Restart
            ), label = "loading skeleton"
        )
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset.Zero,
            end = Offset(x = transitionAnimation.value, y = transitionAnimation.value)
        )

        Crossfade(
            targetState = state.isLoading, label = "Screen animation"
        ) {
            when (it) {
                true -> {
                    LoadingScreen(
                        brush = brush,
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
                    SummaryScreen(modifier = Modifier.consumeWindowInsets(paddingValues),
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                        brush = brush,
                        usageStatsState = state.usageStatsState,
                        customHabitState = state.customHabitState,
                        habitsState = state.habitsState,
                        onSelectDayOnHistory = { date ->
                            summaryViewModel.onEvent(SummaryUiEvent.SelectDayOnHistory(date))
                        },
                        onOpenHabit = { habitId -> onOpenHabitDetails(habitId) },
                        onStart = { habitId ->
                            onStartHabit(habitId)
                        },
                        onOpenUsageStats = onOpenUsageStats,
                        multipleClicksCutter = multipleClicksCutter,
                        onOpenHabitStatistics = {

                        },
                        onSave = { summaryViewModel.onEvent(SummaryUiEvent.SaveHabit) },
                        onShowMessage = { message ->
                            summaryViewModel.onEvent(
                                SummaryUiEvent.ShowMessage(
                                    message
                                )
                            )
                        },
                        onChangeHabitDays = { days ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitDays(days))
                        },
                        onChangeHabitFrequency = { frequency ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitFrequency(frequency))
                        },
                        onChangeHabitIcon = { icon ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitIcon(icon))

                        },
                        onChangeHabitName = { name ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitName(name))
                        },
                        onChangeHabitDuration = { duration ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitDuration(duration))
                        },
                        onChangeHabitRemindTime = { time ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeRemindTime(time))

                        },
                        onChangeHabitStartTime = { time ->
                            summaryViewModel.onEvent(SummaryUiEvent.ChangeHabitStartTime(time))

                        })
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SummaryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    brush: Brush,
    usageStatsState: UsageStatsState,
    customHabitState: CustomHabitState,
    habitsState: HabitsState,
    onSelectDayOnHistory: (Date) -> Unit,
    onOpenHabit: (Long) -> Unit,
    onStart: (Long) -> Unit,
    onOpenUsageStats: () -> Unit,
    multipleClicksCutter: MultipleClicksCutter,
    onOpenHabitStatistics: () -> Unit,
    onSave: () -> Unit,
    onShowMessage: (UserMessage) -> Unit,
    onChangeHabitName: (String) -> Unit,
    onChangeHabitIcon: (String) -> Unit,
    onChangeHabitFrequency: (HabitFrequency) -> Unit,
    onChangeHabitDays: (List<LocalDate>) -> Unit,
    onChangeHabitStartTime: (Long) -> Unit,
    onChangeHabitDuration: (Long) -> Unit,
    onChangeHabitRemindTime: (Long) -> Unit,
) {

    val context = LocalContext.current

    val listState = rememberLazyListState()

    var bottomSheetIsVisble by rememberSaveable {
        mutableStateOf(false)
    }

    val habitFrequency = remember {
        listOf(
            HabitFrequency.DAILY, HabitFrequency.WEEKLY
        )
    }

    val initialTime = remember {
        mutableStateOf(Calendar.getInstance().apply {
            timeInMillis = customHabitState.startTime
        })
    }


    var habitReminderTimeDialogIsVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var showPicker by rememberSaveable { mutableStateOf(false) }

    var habitDurationDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val pickerState = rememberTimePickerState(
        initialHour = if (initialTime.value.get(Calendar.HOUR_OF_DAY) <= 0L) {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        } else {
            initialTime.value.get(Calendar.HOUR_OF_DAY)
        }, initialMinute = if (initialTime.value.get(Calendar.MINUTE) <= 0L) {
            Calendar.getInstance().get(Calendar.MINUTE)
        } else {
            initialTime.value.get(Calendar.MINUTE)
        }

    )

    val timeFormatter = DateTimeFormatter.ofPattern(
        if (pickerState.is24hour) "hh:mm" else "hh:mm a", Locale.getDefault()
    )

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        appUsageData(
            brush = brush,
            usageStatsState = usageStatsState,
            multipleClicksCutter = multipleClicksCutter,
            onOpenUsageStats = onOpenUsageStats
        )
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.habit_history_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    )
                )
//                TextButton(onClick = { multipleClicksCutter.processEvent(onOpenHabitStatistics) }) {
//                    Text(
//                        text = "See all stats",
//                        style = MaterialTheme.typography.labelSmall,
//                    )
//                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            when (habitsState) {
                HabitsState.Empty -> {}
                HabitsState.Loading -> {
                    CircularProgressIndicator()
                }

                is HabitsState.Success -> {
                    HabitHistoryComponent(
                        habitProgress = habitsState.habitHistoryStateList,
                        onSelectDay = onSelectDayOnHistory
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            when (habitsState) {
                HabitsState.Empty -> {}
                HabitsState.Loading -> {
                    CircularProgressIndicator()
                }

                is HabitsState.Success -> {
                    NotificationCard(habitPerformance = habitsState.habitPerformance,
                        notificationButtonVisible = habitsState.habitDataList.isNotEmpty(),
                        onTakeAction = {
                            onOpenHabit(habitsState.habitDataList.first().habitId)
                        })
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.active_habits_section_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    )
                )
                TextButton(onClick = {
                    bottomSheetIsVisble = true
                }) {
                    Text(
                        text = "Add custom habit",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                habitList(
                    habitsState = habitsState,
                    onOpenHabit = onOpenHabit,
                    context = context,
                    onShowMessage = onShowMessage,
                    onStart = onStart
                )
            }
        }

    }
    if (bottomSheetIsVisble) {
        SummaryBottomSheet(onDismiss = { bottomSheetIsVisble = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ), verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add new habit", style = MaterialTheme.typography.headlineSmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = customHabitState.habitName,
                        onValueChange = onChangeHabitName,
                        label = {
                            Text(
                                text = "Name"
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Ex. Take a glass of water",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall

                            )
                        },
                        singleLine = true,
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = customHabitState.habitIcon,
                        onValueChange = onChangeHabitIcon,
                        label = {
                            Text(
                                text = "Icon", style = MaterialTheme.typography.labelSmall
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = initialTime.value.toInstant()
                            .atZone(ZoneId.systemDefault()).format(timeFormatter),
                        onValueChange = { },
                        label = {
                            Text(
                                text = "Start time", style = MaterialTheme.typography.labelSmall

                            )
                        },
                        placeholder = {
                            Text(
                                text = "Ex. 08:30", style = MaterialTheme.typography.labelSmall
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickableOnce {
                                showPicker = true
                            },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                            )
                        },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = getTimeFromMillis(customHabitState.habitDuration),
                        modifier = Modifier
                            .weight(1f)
                            .clickableOnce { habitDurationDialogVisible = true },
                        onValueChange = { },
                        label = {
                            Text(
                                text = "Duration", style = MaterialTheme.typography.labelSmall
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                            )
                        },
                        singleLine = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledBorderColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ), disabledTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (frequency in habitFrequency) {
                            val animatedBg =
                                animateColorAsState(targetValue = if (customHabitState.habitFrequency == frequency) MaterialTheme.colorScheme.primary else Color.Transparent)

                            val boxShape = if (habitFrequency.indexOf(frequency) == 0) {
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    bottomStart = 4.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            } else {
                                RoundedCornerShape(
                                    topEnd = 4.dp,
                                    bottomEnd = 4.dp,
                                    topStart = 0.dp,
                                    bottomStart = 0.dp
                                )
                            }
                            Box(modifier = Modifier
                                .weight(1f)
                                .clip(
                                    boxShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = boxShape
                                )
                                .background(
                                    color = animatedBg.value, shape = boxShape
                                )
                                .clickableOnce {
                                    onChangeHabitFrequency(frequency)
                                }) {
                                Text(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp, horizontal = 16.dp
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    text = frequency.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    color = if (customHabitState.habitFrequency == frequency) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = customHabitState.habitFrequency == HabitFrequency.DAILY) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = getDaysInAWeek()) { day ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = if (customHabitState.days.any { it == day }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.1f
                                            ),
                                            shape = CircleShape
                                        )
                                        .background(
                                            color = if (customHabitState.days.any { it == day }) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickableOnce {
                                            val newList = customHabitState.days.toMutableList()
                                            if (customHabitState.days.any { it == day }) {
                                                newList.remove(day)
                                            } else {
                                                newList.add(day)
                                            }
                                            onChangeHabitDays(newList)
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        style = MaterialTheme.typography.labelMedium,
                                        text = day.dayOfWeek.name.first().uppercase(),
                                        color = if (day in customHabitState.days) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(value = formatDurationMillis(customHabitState.remindTime),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    label = {
                        Text(
                            text = "Remind", style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        ), unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        ), disabledBorderColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        ), disabledTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableOnce { habitReminderTimeDialogIsVisible = true })
                Button(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        if (customHabitState.habitName.isEmpty() || customHabitState.startTime == 0L || customHabitState.habitDuration == 0L) {
                            onShowMessage(UserMessage(message = context.getString(com.githukudenis.intimo.feature.habit.R.string.invalid_details)))
                            return@Button
                        }
                        onSave()
                        bottomSheetIsVisble = false
                    }) {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            if (habitReminderTimeDialogIsVisible) {
                HabitDurationDialog(title = "Remind in", durationList = listOf(
                    0L,
                    5000L * 60,
                    10000L * 60,
                    15000L * 60,
                    30000L * 60,
                ), durationValue = customHabitState.remindTime, onDismissRequest = { duration ->
                    onChangeHabitRemindTime(duration)
                    habitReminderTimeDialogIsVisible = false
                })
            }
            if (habitDurationDialogVisible) {
                HabitDurationDialog(title = "Habit duration",
                    durationValue = customHabitState.habitDuration,
                    onDismissRequest = { duration ->
                        onChangeHabitDuration(duration)
                        habitDurationDialogVisible = false
                    })
            }
            if (showPicker) {
                TimePickerDialog(onCancel = {
                    showPicker = false
                }, onConfirm = {
                    if (pickerState.hour == initialTime.value.get(Calendar.HOUR_OF_DAY) && pickerState.minute == initialTime.value.get(
                            Calendar.MINUTE
                        )
                    ) {
                        return@TimePickerDialog
                    }
                    val habitTime = initialTime.value.apply {
                        set(Calendar.HOUR_OF_DAY, pickerState.hour)
                        set(Calendar.MINUTE, pickerState.minute)
                    }
                    onChangeHabitStartTime(habitTime.timeInMillis)
                    showPicker = false
                }) {
                    TimePicker(state = pickerState)
                }
            }

        }
    }
}

fun LazyListScope.appUsageData(
    usageStatsState: UsageStatsState,
    multipleClicksCutter: MultipleClicksCutter,
    brush: Brush,
    onOpenUsageStats: () -> Unit
) {
    item {
        Surface(
            onClick = { multipleClicksCutter.processEvent(onOpenUsageStats) },
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.1f
                )
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Crossfade(
                targetState = usageStatsState, label = "usage_stats", animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
                )
            ) { state ->
                when (state) {
                    UsageStatsState.Loading -> {
                        UsageStatsShimmerCard(brush = brush)
                    }

                    is UsageStatsState.Loaded -> {
                        val context = LocalContext.current
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.screen_time),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.7f
                                ),
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(8.dp)
                            )
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
                                val totalAppUsage = remember(state.usageStats) {
                                    state.usageStats.sumOf { usageStat -> usageStat.usageDuration.toInt() }
                                        .toFloat()
                                }

                                /*
                                    splice most four used apps
                                     */
                                val fourMostUsedAppDurations = state.usageStats.take(4)
                                    .map { app -> app.usageDuration.toFloat() }.toMutableList()


                                /*
                                    get sum of remaining values
                                     */
                                val remainingTotalUsage = state.usageStats.drop(4)
                                    .sumOf { usage -> usage.usageDuration.toInt() }.toFloat()


                                /*
                                    add remaining usage to first four apps
                                     */
                                fourMostUsedAppDurations.add(remainingTotalUsage)

                                /*
                                    values to be plotted on canvas
                                     */
                                val plotValues = fourMostUsedAppDurations.map { duration ->
                                    duration * 100 / totalAppUsage
                                }


                                val animateArchValue = remember {
                                    Animatable(0f)
                                }

                                LaunchedEffect(key1 = plotValues) {
                                    animateArchValue.animateTo(
                                        targetValue = 1f, animationSpec = tween(
                                            durationMillis = 1000, easing = EaseOut
                                        )
                                    )
                                }

                                /*
                                    derive plot angles
                                     */
                                val angles = plotValues.map { value ->
                                    value * 360f / 100
                                }


                                val textMeasurer = rememberTextMeasurer()
                                val totalAppTime =
                                    state.usageStats.sumOf { appUsage -> appUsage.usageDuration }
                                val totalAppTimeText = TimeFormatter.getTimeFromMillis(totalAppTime)


                                val textLayoutResult = remember(totalAppTimeText) {
                                    textMeasurer.measure(totalAppTimeText)
                                }
                                val labelMedium = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier
                                    .size(120.dp)
                                    .drawWithCache {
                                        var startAngle = -90f

                                        onDrawBehind {
                                            for (i in angles.indices) {/*
                                                        Retrieve color generated from icon or use secondary app color
                                                        */
                                                val arcColor =
                                                    fourMostUsedAppDurations.map { duration ->
                                                        state.usageStats.find { usageStat -> usageStat.usageDuration.toFloat() == duration }
                                                    }[i]?.colorSwatch ?: (0xFF3A5BAB).toInt()

                                                drawArc(
                                                    color = Color(arcColor),
                                                    startAngle = startAngle,
                                                    sweepAngle = angles[i] * animateArchValue.value,
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
                                            state.usageStats.find { app -> app.usageDuration.toFloat() == usage }?.packageName?.let { packageName ->
                                                getApplicationLabel(
                                                    packageName, context
                                                )
                                            } ?: "Other"

                                        /*
                                        Generate total use time for each
                                        Use the value of other summed apps
                                         */
                                        val upTime =
                                            state.usageStats.find { app -> app.usageDuration.toFloat() == usage }?.usageDuration
                                                ?: fourMostUsedAppDurations.last().toLong()

                                        val formattedTime = TimeFormatter.getTimeFromMillis(upTime)


                                        /*
                                        Retrieve color generated from icon or use secondary app color
                                         */
                                        val color =
                                            state.usageStats.find { app -> app.usageDuration.toFloat() == usage }?.colorSwatch
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
                                modifier = Modifier.height(1.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                CardInfo(
                                    title = "Unlocks", value = "${state.unlockCount}"
                                )
                                CardInfo(
                                    title = "Notifications", value = "${state.notificationCount}"
                                )
                            }
                        }
                    }

                    UsageStatsState.Empty -> {}
                }
            }

        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    brush: Brush,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
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
                        shape = MaterialTheme.shapes.medium, border = BorderStroke(
                            width = 1.dp, brush = brush
                        )
                    )
                    .padding(12.dp)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                    Spacer(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth(0.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush = brush)
                    )
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
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
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
    habitsState: HabitsState,
    context: Context,
    onOpenHabit: (Long) -> Unit,
    onShowMessage: (UserMessage) -> Unit,
    onStart: (Long) -> Unit
) {

    when (habitsState) {
        HabitsState.Empty -> {}
        HabitsState.Loading -> {
            item {
                Box(
                    modifier = Modifier.height(120.dp), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is HabitsState.Success -> {
            items(items = habitsState.habitDataList, key = { it.habitId }) { habitUiModel ->
                HabitCard(habitUiModel = habitUiModel,
                    isRunning = habitsState.runningHabitState.habitId == habitUiModel.habitId,
                    onOpenHabitDetails = { habitId ->
                        onOpenHabit(habitId)
                    },
                    onStart = { habitId ->
                        if (habitsState.runningHabitState.habitId != null && habitsState.runningHabitState.habitId != habitId) {
                            onShowMessage(
                                UserMessage(
                                    id = 1,
                                    message = context.getString(R.string.multiple_habit_running_error_text),
                                    messageType = MessageType.ERROR(dismissable = true)
                                )
                            )
                            return@HabitCard
                        }
                        onStart(habitId)
                    })
            }
        }
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

//@Preview
//@Composable
//fun SummaryScreenPrev() {
//    SummaryScreen(
//        contentPadding = PaddingValues(12.dp),
//        habitDataList = listOf(
//            HabitUiModel(
//                Pair(0L, false),
//                10000L,
//                2,
//                "\uD83E\uDEC1",
//                HabitType.BREATHING,
//                startTime = 1627350000L,
//                duration = 30000L,
//                durationType = DurationType.MINUTE
//            ),
//            HabitUiModel(
//                Pair(0L, true),
//                1000L,
//                3,
//                "\uD83C\uDFC3",
//                HabitType.EXERCISE,
//                startTime = 1627350000L,
//                duration = 30000L,
//                durationType = DurationType.MINUTE
//            ),
//            HabitUiModel(
//                Pair(0L, false),
//                1000L,
//                4,
//                "\uD83D\uDCDA",
//                HabitType.READING,
//                startTime = 1627350000L,
//                duration = 30000L,
//                durationType = DurationType.MINUTE
//            ),
//        ),
//        onSelectDayOnHistory = {},
//        onOpenHabit = {},
//        onStart = {},
//        onOpenUsageStats = {},
//        habitsState = HabitsState.Loading,
//        usageStatsState = UsageStatsState.Loading,
//        multipleClicksCutter = remember {
//            MultipleClicksCutter.get()
//        }
//    )
//}


@Preview
@Composable
fun LoadingScreenPrev() {
    LoadingScreen(brush = Brush.linearGradient(colors = listOf()))
}