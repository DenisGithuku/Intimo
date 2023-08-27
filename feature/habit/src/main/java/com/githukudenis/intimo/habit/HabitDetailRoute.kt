package com.githukudenis.intimo.habit


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.ui.components.TimePickerDialog
import com.githukudenis.intimo.core.ui.components.rememberDateUiState
import com.githukudenis.intimo.habit.components.DatePill
import com.githukudenis.intimo.habit.components.HabitDetailListItem
import com.githukudenis.model.HabitType
import com.githukudenis.model.nameToString
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailRoute(
    habitDetailViewModel: HabitDetailViewModel = hiltViewModel(),
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
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
        )
    }


}

@Composable
private fun HabitDetailScreen(
    modifier: Modifier = Modifier,
    uiState: HabitDetailUiState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val dateUiState = rememberDateUiState(LocalDate.now())

        Text(
            text = "History",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
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
        uiState.habitUiModel?.let { HabitContents(habitDetail = it, onUpdate = {}) }
    }
}


@Composable
fun HorizontalDateView(
    selectedDate: LocalDate,
    dates: List<Date>,
    completedDates: List<LocalDate>,
    onPrevWeekListener: (LocalDate) -> Unit,
    onNextWeekListener: (LocalDate) -> Unit,
    onChangeDate: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onPrevWeekListener(selectedDate) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(id = R.string.prev_week),
                )
            }
            Text(
                text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                style = MaterialTheme.typography.labelMedium
            )
            IconButton(
                enabled = selectedDate < LocalDate.now(),
                onClick = { onNextWeekListener(selectedDate) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.next_week),
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = dates, key = { it.date }) { dateItem ->
                DatePill(
                    dateItem = dateItem,
                    selected = dateItem.date == selectedDate,
                    onChangeDate = onChangeDate,
                    completed = dateItem.date in completedDates
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

    val pickerState = rememberTimePickerState(
        initialHour = habitTime.value.get(Calendar.HOUR_OF_DAY),
        initialMinute = habitTime.value.get(Calendar.MINUTE)
    )

    var habitTimeChanged by remember {
        mutableStateOf(false)
    }

    val timeFormatter = DateTimeFormatter.ofPattern(
        if (pickerState.is24hour) "hh:mm" else "hh:mm a",
        Locale.getDefault()
    )
    val pickerIsVisible = rememberSaveable {
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
                    style = MaterialTheme.typography.headlineSmall,
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
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            description = {
                Text(
                    text = getTimeFromMillis(habitDetail.duration),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onClick = {
                pickerIsVisible.value = true
            },
        )
        AnimatedVisibility(
            visible = habitTimeChanged,
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
    HabitContents(habitDetail = HabitUiModel(habitIcon = "", habitType = HabitType.EXERCISE)) {}
}