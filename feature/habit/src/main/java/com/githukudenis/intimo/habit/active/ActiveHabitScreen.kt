package com.githukudenis.intimo.habit.active

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.habit.R
import com.githukudenis.intimo.habit.components.CountDownTimer
import com.githukudenis.model.nameToString

@Composable
fun ActiveHabitRoute(
    viewModel: HabitActiveViewModel = hiltViewModel(),
    onHabitCompleted: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val remainingTime = remember {
        mutableLongStateOf(state.habitData?.duration ?: 0L)
    }

    val currentOnHabitCompleted by rememberUpdatedState(onHabitCompleted)

    val context = LocalContext.current

    state.habitData?.let { habitData ->
        ActiveHabitScreen(
            activeHabitUiState = state,
            onStartTimer = {
                startTimerService(
                    context,
                    habitData.habitType.nameToString(),
                    context.getString(R.string.habit_notification_description),
                    duration = remainingTime.longValue
                )
            },
            onTimeChanged = { currentTime ->
                viewModel.onTimeChanged(currentTime)
            },
            onTimerFinished = {
                viewModel.onTimerFinished()
                currentOnHabitCompleted()
            },
            onPauseTimer = { remTime ->
                remainingTime.longValue = remTime
                stopTimerService(context)
            }
        )
    }
}

@Composable
internal fun ActiveHabitScreen(
    activeHabitUiState: ActiveHabitUiState,
    onTimeChanged: (Long) -> Unit,
    onStartTimer: () -> Unit,
    onTimerFinished: () -> Unit,
    onPauseTimer: (Long) -> Unit,
) {

    val (totalTime, currentTime) = activeHabitUiState.timerState

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        activeHabitUiState.habitData?.habitType?.let { habitType ->
            Text(
                text = habitType.nameToString(),
                style = MaterialTheme.typography.titleMedium
            )
        }

        totalTime?.let { totalTime ->
            currentTime?.let { currentTime ->
                activeHabitUiState.timerState.timerButtonStatusText?.let {
                    CountDownTimer(
                        modifier = Modifier.size(200.dp),
                        totalTime = totalTime,
                        currentTime = currentTime,
                        isTimerRunning = activeHabitUiState.timerState.isRunning,
                        buttonStatusText = it,
                        onStartTimer = onStartTimer,
                        onTimeChanged = onTimeChanged,
                        onTimerFinished = onTimerFinished,
                        onPauseTimer = onPauseTimer
                    )
                }
            }
        }
    }
}

fun stopTimerService(context: Context) {
    Intent(context, ActiveHabitService::class.java).apply {
        action = ActiveHabitService.NotificationAction.STOP.toString()
    }.also {
        context.stopService(it)
    }
}

fun startTimerService(
    context: Context,
    title: String,
    content: String,
    duration: Long
) {
    val intent = Intent(context, ActiveHabitService::class.java).apply {
        putExtra("title", title)
        putExtra("content", content)
        putExtra("duration", duration)
        action = ActiveHabitService.NotificationAction.START.toString()
    }
    context.startService(intent)
}