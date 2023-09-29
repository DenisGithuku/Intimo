package com.githukudenis.intimo.feature.habit.detail


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.ui.components.MultipleClicksCutter
import com.githukudenis.intimo.core.ui.components.TimePickerDialog
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.core.ui.components.get
import com.githukudenis.intimo.core.ui.components.rememberDateUiState
import com.githukudenis.intimo.core.util.TimeFormatter.Companion.getTimeFromMillis
import com.githukudenis.intimo.feature.habit.R
import com.githukudenis.intimo.feature.habit.components.DatePill
import com.githukudenis.intimo.feature.habit.components.HabitDetailListItem
import com.githukudenis.intimo.feature.habit.components.HabitDurationDialog
import com.githukudenis.intimo.feature.habit.components.HorizontalDateView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailRoute(
    habitDetailViewModel: HabitDetailViewModel = hiltViewModel(),
    onStartHabit: (Long) -> Unit,
    onNavigateUp: () -> Unit
) {
    val uiState by habitDetailViewModel.uiState.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val multipleClicksCutter = remember {
        MultipleClicksCutter.get()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                uiState.habitUiModel?.habitName?.let {
                    Text(
                        text = it
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { multipleClicksCutter.processEvent(onNavigateUp) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            }, scrollBehavior = topAppBarScrollBehavior
            )
        },
    ) { paddingValues ->
        HabitDetailScreen(
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPaddingValues = paddingValues,
            uiState = uiState,
            onStartHabit = onStartHabit,
            onUpdate = { habitUiModel ->
                habitDetailViewModel.onUpdate(habitUiModel)
            },
            multipleClicksCutter = multipleClicksCutter
        )
    }


}

@Composable
private fun HabitDetailScreen(
    modifier: Modifier = Modifier,
    uiState: HabitDetailUiState,
    contentPaddingValues: PaddingValues = PaddingValues(16.dp),
    onStartHabit: (Long) -> Unit,
    multipleClicksCutter: MultipleClicksCutter = remember {
        MultipleClicksCutter.get()
    },
    onUpdate: (HabitUiModel) -> Unit
) {
    Column(
        modifier = modifier
            .padding(
                top = contentPaddingValues.calculateTopPadding(),
                bottom = contentPaddingValues.calculateBottomPadding()
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val dateUiState = rememberDateUiState(LocalDate.now())

        Column {
            Text(
                text = "History",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
            )

            HorizontalDateView(
                selectedDate = dateUiState.currentSelectedDate,
                dates = dateUiState.dateUiModel.availableDates,
                onNextWeekListener = { localDate ->
                    if (dateUiState.currentSelectedDate < LocalDate.now()) {
                        dateUiState.setData(
                            localDate.plusDays(1)
                        )
                        dateUiState.updateDate(localDate.plusDays(1))
                    }
                },
                onPrevWeekListener = { localDate ->
                    dateUiState.setData(localDate.minusDays(2))
                    dateUiState.updateDate(localDate.minusDays(2))
                },
                onChangeDate = {
                    dateUiState.updateDate(it)
                },
                completedDates = uiState.completedDayList
            )
            Divider(
                thickness = 0.7.dp, modifier = Modifier.fillMaxWidth()
            )
            uiState.habitUiModel?.let {
                HabitContents(habitDetail = it, onUpdate = onUpdate)
            }
        }
        if (uiState.habitUiModel?.completed == false) {
            Button(
                onClick = {
                    multipleClicksCutter.processEvent {
                        uiState.habitId?.let {
                            onStartHabit(
                                it
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (uiState.habitUiModel.running) "See progress" else "Start habit",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(6.dp),

                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitContents(
    habitDetail: HabitUiModel, onUpdate: (HabitUiModel) -> Unit
) {

    val initialTime = rememberSaveable {
        Calendar.getInstance().apply {
            timeInMillis = habitDetail.startTime
        }
    }


    val habitTime = rememberSaveable(habitDetail.startTime) {
        mutableStateOf(Calendar.getInstance().apply {
            timeInMillis = habitDetail.startTime
        })
    }

    val selectedHabitDuration = rememberSaveable(habitDetail.duration) {
        mutableStateOf(habitDetail.duration)
    }

    val pickerState = rememberTimePickerState(
        initialHour = habitTime.value.get(Calendar.HOUR_OF_DAY),
        initialMinute = habitTime.value.get(Calendar.MINUTE)
    )

    val timeFormatter = DateTimeFormatter.ofPattern(
        if (pickerState.is24hour) "hh:mm" else "hh:mm a", Locale.getDefault()
    )
    val pickerIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    var habitDurationDialogVisible by remember {
        mutableStateOf(false)
    }

    var bottomSheetIsVisible by remember { mutableStateOf(false) }

    var habitReminderTimeDialogIsVisible by remember { mutableStateOf(false) }

    val habitFrequency = remember {
        listOf(HabitFrequency.DAILY, HabitFrequency.WEEKLY)
    }

    val selectedFrequency = remember {
        mutableStateOf(habitDetail.frequency)
    }

    val selectedDays = remember {
        mutableStateOf(habitDetail.selectedDays)
    }

    val selectedReminderDuration = remember {
        mutableStateOf(habitDetail.remindBefore)
    }

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HabitDetailListItem(icon = {
            Icon(
                imageVector = Icons.Outlined.Start,
                contentDescription = stringResource(id = R.string.habit_start_time),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }, label = {
            Text(
                text = "Start time",
                style = MaterialTheme.typography.bodyMedium,
            )
        }, description = {
            Text(
                text = habitTime.value.toInstant().atZone(ZoneId.systemDefault())
                    .format(timeFormatter), style = MaterialTheme.typography.labelSmall
            )
        }, onClick = {
            pickerIsVisible.value = true
        })
        HabitDetailListItem(
            icon = {
                Icon(
                    Icons.Outlined.AccessTime,
                    contentDescription = stringResource(R.string.habit_start_time),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            },
            label = {
                Text(
                    text = "Duration", style = MaterialTheme.typography.bodyMedium
                )
            },
            description = {
                Text(
                    text = getTimeFromMillis(selectedHabitDuration.value),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onClick = {
                habitDurationDialogVisible = true
            },
        )
        HabitDetailListItem(icon = {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = stringResource(R.string.frequency),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }, label = {
            Text(
                text = "Frequency", style = MaterialTheme.typography.bodyMedium
            )
        }, description = {
            Text(
                text = selectedFrequency.value.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall
            )
        }, onClick = {
            bottomSheetIsVisible = true
        })
        HabitDetailListItem(icon = {
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = stringResource(R.string.habit_reminder),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }, label = {
            Text(
                text = "Remind", style = MaterialTheme.typography.bodyMedium
            )
        }, description = {
            Text(
                text = formatDurationMillis(selectedReminderDuration.value), style = MaterialTheme.typography.labelSmall
            )
        }, onClick = {
            habitReminderTimeDialogIsVisible = true
        })
    }

    if (bottomSheetIsVisible) {
        ModalBottomSheet(onDismissRequest = {
            if (habitDetail.frequency != selectedFrequency.value || habitDetail.selectedDays != selectedDays.value) {
                onUpdate(
                    habitDetail.copy(
                        frequency = selectedFrequency.value, selectedDays = selectedDays.value
                    )
                )
            }
            bottomSheetIsVisible = false
        }) {
            Column(modifier = Modifier.padding(
                end = 16.dp,
                start = 16.dp,
                bottom = 16.dp
            )) {
                Text(
                    text = "Habit Frequency",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.7f
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    for (frequency in habitFrequency) {
                        val animatedBg =
                            animateColorAsState(targetValue = if (selectedFrequency.value == frequency) MaterialTheme.colorScheme.primary else Color.Transparent)

                        val boxShape = if (habitFrequency.indexOf(frequency) == 0) {
                            RoundedCornerShape(
                                topStart = 100.dp,
                                bottomStart = 100.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            )
                        } else {
                            RoundedCornerShape(
                                topEnd = 100.dp,
                                bottomEnd = 100.dp,
                                topStart = 0.dp,
                                bottomStart = 0.dp
                            )
                        }
                        Box(modifier = Modifier
                            .clip(
                                boxShape
                            )
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = boxShape)
                            .background(
                                color = animatedBg.value,
                                shape = boxShape
                            )
                            .clickableOnce {
                                selectedFrequency.value = frequency
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                style = MaterialTheme.typography.labelMedium,
                                text = frequency.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                color = if (selectedFrequency.value == frequency) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.7f
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(visible = selectedFrequency.value == HabitFrequency.DAILY) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = getDaysInAWeek()) { day ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedDays.value.any { it == day }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.1f
                                        ),
                                        shape = CircleShape
                                    )
                                    .background(
                                        color = if (selectedDays.value.any { it == day }) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickableOnce {
                                        val newList = selectedDays.value.toMutableList()
                                        if (selectedDays.value.any { it == day }) {
                                            newList.remove(day)
                                        } else {
                                            newList.add(day)
                                        }
                                        selectedDays.value = newList
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    style = MaterialTheme.typography.labelMedium,
                                    text = day.dayOfWeek.name.first().uppercase(),
                                    color = if (day in selectedDays.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { bottomSheetIsVisible = false }) {
                        Text(
                            text = "Cancel", style = MaterialTheme.typography.labelMedium
                        )
                    }
                    TextButton(onClick = {
                        onUpdate(
                            habitDetail.copy(
                                selectedDays = selectedDays.value,
                                frequency = selectedFrequency.value
                            )
                        )
                        bottomSheetIsVisible = false
                    }) {
                        Text(
                            text = "Save", style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
    if (pickerIsVisible.value) {
        TimePickerDialog(onCancel = {
            habitTime.value = initialTime
            pickerIsVisible.value = false
        }, onConfirm = {
            if (pickerState.hour == initialTime.get(Calendar.HOUR_OF_DAY) && pickerState.minute == initialTime.get(
                    Calendar.MINUTE
                )
            ) {
                return@TimePickerDialog
            }
            habitTime.value = habitTime.value.apply {
                set(Calendar.HOUR_OF_DAY, pickerState.hour)
                set(Calendar.MINUTE, pickerState.minute)
            }
            onUpdate(
                habitDetail.copy(
                    startTime = habitTime.value.timeInMillis
                )
            )
            pickerIsVisible.value = false
        }) {
            TimePicker(state = pickerState)
        }
    }
    if (habitDurationDialogVisible) {
        HabitDurationDialog(
            title = "Habit duration",
            durationValue = selectedHabitDuration.value,
            onDismissRequest = { duration ->
                selectedHabitDuration.value = duration
                if (duration != habitDetail.duration) {
                    onUpdate(
                        habitDetail.copy(
                            duration = selectedHabitDuration.value,
                            durationType = if (selectedHabitDuration.value >= 1000L * 60 * 60) DurationType.HOUR else DurationType.MINUTE
                        )
                    )
                }
                habitDurationDialogVisible = false
            })
    }
    if (habitReminderTimeDialogIsVisible) {
        HabitDurationDialog(title = "Remind in",
            durationList = listOf(
            0L,
            5000L * 60,
            10000L * 60,
            15000L * 60,
            30000L * 60,
            ),
            durationValue = selectedReminderDuration.value,
            onDismissRequest = { duration ->
            selectedReminderDuration.value = duration
            if (duration != habitDetail.remindBefore) {
                onUpdate(
                    habitDetail.copy(
                        remindBefore = selectedReminderDuration.value
                    )
                )
            }
            habitReminderTimeDialogIsVisible = false
        })
    }
}

fun getDaysInAWeek(): List<LocalDate> {
    return (0..6).map { offset ->
        LocalDate.now().with(DayOfWeek.MONDAY).plusDays(offset.toLong())
    }
}

fun formatDurationMillis(timeInMillis: Long): String {
    return if (timeInMillis == 0L) {
        "Never"
    } else if (timeInMillis / 1000 / 60 / 60 >= 1) {
        if (timeInMillis / 1000 / 60 / 60 > 1) "${timeInMillis / 1000 / 60 / 60} hours" else "${timeInMillis / 1000 / 60 / 60} hour"
    } else {
        "${timeInMillis / 1000 / 60 % 60} minutes"
    }
}

fun getTimeFromMillis(timeInMillis: Long): String {
    return when {
        timeInMillis / 1000 / 60 / 60 >= 1 && timeInMillis / 1000 / 60 % 60 == 0L -> {
            "${timeInMillis / 1000 / 60 / 60}hr"
        }

        timeInMillis / 1000 / 60 / 60 >= 1 -> {
            "${timeInMillis / 1000 / 60 / 60}hr ${timeInMillis / 1000 / 60 % 60}min"
        }

        timeInMillis / 1000 / 60 >= 1 -> {
            "${timeInMillis / 1000 / 60}min"
        }

        timeInMillis / 1000 >= 1 -> {
            "Less than a minute"
        }

        else -> {
            "0 min"
        }
    }
}

@Preview
@Composable
fun SelectedPillPrev() {
    DatePill(dateItem = Date(isToday = true), selected = true, onChangeDate = {})
}

@Preview
@Composable
fun UnselectedPillPrev() {
    DatePill(dateItem = Date(isToday = true), selected = false, onChangeDate = {})
}

@Preview(name = "Habit contents preview")
@Composable
fun HabitContentsPrev() {
    HabitContents(
        habitDetail = HabitUiModel(
            habitIcon = "", habitName = "Go for a run", durationType = DurationType.MINUTE
        )
    ) {}
}

@Preview(device = "id:pixel_6a", showBackground = false, showSystemUi = false)
@Composable
fun HabitDurationDialogPrev() {
    HabitDurationDialog(durationValue = 1000L, onDismissRequest = { }, title = "Habit duration")
}