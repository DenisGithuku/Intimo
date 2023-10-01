package com.githukudenis.intimo.feature.habit.add_habit

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.core.ui.components.TimePickerDialog
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.feature.habit.R
import com.githukudenis.intimo.feature.habit.components.HabitDurationDialog
import com.githukudenis.intimo.feature.habit.detail.formatDurationMillis
import com.githukudenis.intimo.feature.habit.detail.getDaysInAWeek
import com.githukudenis.intimo.feature.habit.detail.getTimeFromMillis
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
internal fun AddHabitRoute(
    addHabitViewModel: AddHabitViewModel = hiltViewModel(), onNavigateUp: () -> Unit
) {
    val state by addHabitViewModel.uiState.collectAsStateWithLifecycle()

    AddHabitScreen(state = state,
        onChangeHabitName = { name -> addHabitViewModel.onEvent(AddHabitUiEvent.ChangeHabitName(name)) },
        onChangeStartTime = { startTime ->
            addHabitViewModel.onEvent(
                AddHabitUiEvent.ChangeHabitStartTime(startTime)
            )
        },
        onChangeDuration = { duration ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ChangeHabitDuration(duration))
        },
        onChangeFrequency = { frequency ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ChangeHabitFrequency(frequency))
        },
        onChangeHabitDays = { days ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ChangeHabitDays(days))
        },
        onChangeRemindTime = { time ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ChangeRemindTime(time))
        },
        onNavigateUp = onNavigateUp,
        showUserMessage = { message ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ShowUserMessage(message))
        },
        onSave = {
            addHabitViewModel.onEvent(AddHabitUiEvent.SaveHabit)
            onNavigateUp()
        },
        onChangeHabitIcon = { icon ->
            addHabitViewModel.onEvent(AddHabitUiEvent.ChangeHabitIcon(icon))
        },
        onDismissUserMessage = { id ->
            addHabitViewModel.onEvent(AddHabitUiEvent.DismissUserMessage(id))
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    state: AddHabitUiState,
    onChangeHabitName: (String) -> Unit,
    onChangeStartTime: (Long) -> Unit,
    onChangeDuration: (Long) -> Unit,
    onChangeFrequency: (HabitFrequency) -> Unit,
    onChangeHabitDays: (List<LocalDate>) -> Unit,
    onChangeRemindTime: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    showUserMessage: (String) -> Unit,
    onChangeHabitIcon: (String) -> Unit,
    onDismissUserMessage: (Long) -> Unit,
    onSave: () -> Unit
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val context = LocalContext.current

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = "New habit",
                style = MaterialTheme.typography.headlineSmall
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.Close, contentDescription = "Back"
                )
            }
        }, actions = {
            TextButton(onClick = {
                if (state.habitName.isEmpty() || state.startTime == 0L || state.habitDuration == 0L) {
                    showUserMessage(context.getString(R.string.invalid_details))
                    return@TextButton
                }
                onSave()
            }) {
                Text(
                    text = "Save"
                )
            }
        })
    }) { paddingValues ->

        LaunchedEffect(state.userMessages, snackbarHostState) {
            if (state.userMessages.isNotEmpty()) {
                val userMessage = state.userMessages.first()
                snackbarHostState.showSnackbar(
                    message = userMessage.message ?: "An error occurred",
                )
                onDismissUserMessage(userMessage.id)

            }
        }

        val habitFrequency = remember {
            listOf(
                HabitFrequency.DAILY, HabitFrequency.WEEKLY
            )
        }

        val initialTime = remember {
            mutableStateOf(Calendar.getInstance().apply {
                timeInMillis = state.startTime
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
            initialHour = if(initialTime.value.get(Calendar.HOUR_OF_DAY) <= 0L) {
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            } else {
                initialTime.value.get(Calendar.HOUR_OF_DAY)
            },
            initialMinute = if (initialTime.value.get(Calendar.MINUTE) <= 0L) {
                Calendar.getInstance().get(Calendar.MINUTE)
            } else {
                initialTime.value.get(Calendar.MINUTE)
            }

        )
        val timeFormatter = DateTimeFormatter.ofPattern(
            if (pickerState.is24hour) "hh:mm" else "hh:mm a", Locale.getDefault()
        )

        Column(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)

        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(value = state.habitName,
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
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = state.habitIcon,
                    onValueChange = {
                        Log.d("emoji", it.length.toString())
                        onChangeHabitIcon(
                            it
                        )
                    },
                    label = {
                        Text(
                            text = "Icon"
                        )
                    },
                    singleLine = true, modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(value = initialTime.value.toInstant()
                    .atZone(ZoneId.systemDefault()).format(timeFormatter),
                    readOnly = true,
                    enabled = false,
                    onValueChange = { },
                    label = {
                        Text(
                            text = "Start time"
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Ex. 08:30"
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
                    })
                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = getTimeFromMillis(state.habitDuration),
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .clickableOnce { habitDurationDialogVisible = true },
                    onValueChange = { },
                    label = {
                        Text(
                            text = "Duration"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (frequency in habitFrequency) {
                        val animatedBg =
                            animateColorAsState(targetValue = if (state.habitFrequency == frequency) MaterialTheme.colorScheme.primary else Color.Transparent)

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
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = boxShape
                            )
                            .background(
                                color = animatedBg.value, shape = boxShape
                            )
                            .clickableOnce {
                                onChangeFrequency(frequency)
                            }) {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = 12.dp, horizontal = 16.dp
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                text = frequency.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                color = if (state.habitFrequency == frequency) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.7f
                                )
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = state.habitFrequency == HabitFrequency.DAILY) {
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
                                        color = if (state.days.any { it == day }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.1f
                                        ),
                                        shape = CircleShape
                                    )
                                    .background(
                                        color = if (state.days.any { it == day }) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickableOnce {
                                        val newList = state.days.toMutableList()
                                        if (state.days.any { it == day }) {
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
                                    color = if (day in state.days) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            OutlinedTextField(value = formatDurationMillis(state.remindTime),
                onValueChange = {},
                enabled = false,
                readOnly = true,
                label = { Text(text = "Remind") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableOnce { habitReminderTimeDialogIsVisible = true })

            if (habitReminderTimeDialogIsVisible) {
                HabitDurationDialog(title = "Remind in", durationList = listOf(
                    0L,
                    5000L * 60,
                    10000L * 60,
                    15000L * 60,
                    30000L * 60,
                ), durationValue = state.remindTime, onDismissRequest = { duration ->
                    onChangeRemindTime(duration)
                    habitReminderTimeDialogIsVisible = false
                })
            }
            if (habitDurationDialogVisible) {
                HabitDurationDialog(title = "Habit duration",
                    durationValue = state.habitDuration,
                    onDismissRequest = { duration ->
                        onChangeDuration(duration)
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
                    onChangeStartTime(habitTime.timeInMillis)
                    showPicker = false
                }) {
                    TimePicker(state = pickerState)
                }
            }

        }
    }
}