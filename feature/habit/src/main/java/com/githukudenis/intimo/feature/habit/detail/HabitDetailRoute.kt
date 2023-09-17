package com.githukudenis.intimo.feature.habit.detail


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.model.nameToString
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.ui.components.TimePickerDialog
import com.githukudenis.intimo.core.ui.components.rememberDateUiState
import com.githukudenis.intimo.feature.habit.R
import com.githukudenis.intimo.feature.habit.components.DatePill
import com.githukudenis.intimo.feature.habit.components.HabitDetailListItem
import com.githukudenis.intimo.feature.habit.components.HabitDurationDialog
import com.githukudenis.intimo.feature.habit.components.HorizontalDateView
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    uiState.habitUiModel?.habitType?.nameToString()?.let {
                        Text(
                            text = it
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        },
    ) { paddingValues ->
        HabitDetailScreen(
            modifier = Modifier
                .consumeWindowInsets(paddingValues),
            uiState = uiState,
            onStartHabit = onStartHabit,
            onUpdate = { habitUiModel ->
                habitDetailViewModel.onUpdate(habitUiModel)
                onNavigateUp()
            }
        )
    }


}

@Composable
private fun HabitDetailScreen(
    modifier: Modifier = Modifier,
    uiState: HabitDetailUiState,
    onStartHabit: (Long) -> Unit,
    onUpdate: (HabitUiModel) -> Unit
) {
    Column(
        modifier = modifier
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
                thickness = 0.7.dp,
                modifier = Modifier.fillMaxWidth()
            )
            uiState.habitUiModel?.let {
                HabitContents(habitDetail = it, onUpdate = onUpdate)
            }
        }
        if (uiState.habitUiModel?.completed == false) {
            FilledTonalButton(
                onClick = { uiState.habitId?.let { onStartHabit(it) } },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = if (uiState.habitUiModel.running) "See progress" else "Start habit"
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitContents(
    habitDetail: HabitUiModel,
    onUpdate: (HabitUiModel) -> Unit
) {

    val initialTime = rememberSaveable {
        Calendar.getInstance().apply {
            timeInMillis = habitDetail.startTime
        }
    }


    val habitTime = rememberSaveable {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = habitDetail.startTime
            }
        )
    }

    val habitDuration = rememberSaveable {
        mutableStateOf(habitDetail.duration)
    }

    val pickerState = rememberTimePickerState(
        initialHour = habitTime.value.get(Calendar.HOUR_OF_DAY),
        initialMinute = habitTime.value.get(Calendar.MINUTE)
    )

    var habitTimeChanged by remember {
        mutableStateOf(false)
    }

    var habitDurationChanged by remember {
        mutableStateOf(false)
    }

    val timeFormatter = DateTimeFormatter.ofPattern(
        if (pickerState.is24hour) "hh:mm" else "hh:mm a",
        Locale.getDefault()
    )
    val pickerIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    var habitDurationDialogVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HabitDetailListItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Start,
                    contentDescription = stringResource(id = R.string.habit_start_time)
                )
            },
            label = {
                Text(
                    text = "Start time",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            description = {
                Text(
                    text = habitTime.value
                        .toInstant().atZone(ZoneId.systemDefault()).format(timeFormatter),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onClick = {
                pickerIsVisible.value = true
            }
        )
        HabitDetailListItem(
            icon = {
                Icon(
                    Icons.Outlined.AccessTime,
                    contentDescription = stringResource(R.string.habit_start_time)
                )
            },
            label = {
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            description = {
                Text(
                    text = getTimeFromMillis(habitDuration.value),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onClick = {
                habitDurationDialogVisible = true
            },
        )
        AnimatedVisibility(
            visible = habitTimeChanged || habitDurationChanged,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Button(onClick = {
                onUpdate(
                    habitDetail.copy(
                        startTime = habitTime.value.apply {
                            set(Calendar.MILLISECOND, 0)
                            set(Calendar.SECOND, 0)
                        }.timeInMillis,
                        duration = habitDuration.value
                    )
                )
            }) {
                Text(
                    text = "Update"
                )
            }
        }
    }

    if (pickerIsVisible.value) {
        TimePickerDialog(
            onCancel = {
                habitTime.value = initialTime
                pickerIsVisible.value = false
                habitTimeChanged = false
            },
            onConfirm = {
                if (pickerState.hour == initialTime.get(Calendar.HOUR_OF_DAY) && pickerState.minute == initialTime.get(
                        Calendar.MINUTE
                    )
                ) {
                    habitTimeChanged = false
                    pickerIsVisible.value = false
                    return@TimePickerDialog
                }
                habitTime.value = habitTime.value.apply {
                    set(Calendar.HOUR_OF_DAY, pickerState.hour)
                    set(Calendar.MINUTE, pickerState.minute)
                }
                habitTimeChanged = true
                pickerIsVisible.value = false
            }
        ) {
            TimePicker(state = pickerState)
        }
    }
    if (habitDurationDialogVisible) {
        HabitDurationDialog(
            durationValue = habitDetail.duration,
            onDismissRequest = { duration ->
                habitDuration.value = duration
                habitDurationChanged = duration != habitDetail.duration
                habitDurationDialogVisible = false
            }
        )
    }
}

fun formatDurationMillis(timeInMillis: Long): String {
    return if (timeInMillis / 1000 / 60 / 60 >= 1) {
        if (timeInMillis / 1000 / 60 / 60 > 1)
            "${timeInMillis / 1000 / 60 / 60} hours" else "${timeInMillis / 1000 / 60 / 60} hour"
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
            habitIcon = "",
            habitType = HabitType.EXERCISE,
            durationType = DurationType.MINUTE
        )
    ) {}
}

@Preview(device = "id:pixel_6a", showBackground = false, showSystemUi = false)
@Composable
fun HabitDurationDialogPrev() {
    HabitDurationDialog(
        durationValue = 1000L,
        onDismissRequest = { }
    )
}