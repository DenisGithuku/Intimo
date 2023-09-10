package com.githukudenis.intimo.habit.active

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.habit.R
import com.githukudenis.intimo.habit.components.CountDownTimer
import com.githukudenis.model.HabitType
import com.githukudenis.model.nameToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveHabitRoute(
    viewModel: HabitActiveViewModel = hiltViewModel(),
    onHabitCompleted: () -> Unit,
    onNavigateUp: () -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { }, navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            })
        }
    ) { paddingValues ->
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        val currentOnHabitCompleted by rememberUpdatedState(onHabitCompleted)

        val context = LocalContext.current

        state.habitData?.let { habitData ->
            ActiveHabitScreen(
                modifier = Modifier.padding(paddingValues),
                activeHabitUiState = state,
                onStartTimer = {
                    viewModel.onStartHabit()
                    startTimerService(
                        context,
                        habitData.habitType.nameToString(),
                        content = context.getString(R.string.habit_notification_description),
                        duration = state.timerState.currentTime,
                        habitId = habitData.habitId,
                        habitType = habitData.habitType
                    )
                },
                onPauseTimer = {
                    stopTimerService(context)
                },
                onResumeTimer = {
                    startTimerService(
                        context,
                        habitData.habitType.nameToString(),
                        content = context.getString(R.string.habit_notification_description),
                        duration = state.timerState.currentTime,
                        habitId = habitData.habitId,
                        habitType = habitData.habitType
                    )
                },
                onRestartHabit = {
                    viewModel.onRestartHabit()
                    stopTimerService(context)
                    startTimerService(
                        context,
                        habitData.habitType.nameToString(),
                        content = context.getString(R.string.habit_notification_description),
                        duration = habitData.duration,
                        habitId = habitData.habitId,
                        habitType = habitData.habitType
                    )

                },
                onCancelHabit = {
                    viewModel.onCancelHabit()
                    stopTimerService(context)
                    onNavigateUp()
                },
                onTimerFinished = {
                    viewModel.onCompleteHabit()
                    currentOnHabitCompleted()
                }
            )
        }
    }
}

@Composable
internal fun ActiveHabitScreen(
    modifier: Modifier = Modifier,
    activeHabitUiState: ActiveHabitUiState,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onRestartHabit: () -> Unit,
    onCancelHabit: () -> Unit,
    onTimerFinished: () -> Unit,
) {

    val (totalTime, currentTime) = activeHabitUiState.timerState

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        activeHabitUiState.habitData?.let { habit ->


            CountDownTimer(
                modifier = Modifier.size(200.dp),
                totalTime = totalTime,
                habitName = habit.habitType.nameToString(),
                currentTime = currentTime,
                isTimerRunning = activeHabitUiState.timerState.isRunning,
                onStartTimer = onStartTimer,
                onPauseTimer = onPauseTimer,
                onResumeTimer = onResumeTimer,
                onRestartHabit = onRestartHabit,
                onCancelHabit = onCancelHabit,
                onTimerFinished = onTimerFinished
            )
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
    habitType: HabitType,
    duration: Long,
    habitId: Long
) {
    val intent = Intent(context, ActiveHabitService::class.java).apply {
        putExtra("title", title)
        putExtra("content", content)
        putExtra("duration", duration)
        putExtra("habitId", habitId)
        putExtra("habitType", habitType.name)
        action = ActiveHabitService.NotificationAction.START.toString()
    }
    context.startService(intent)
}