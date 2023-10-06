package com.githukudenis.intimo.feature.habit.active

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.model.nameToString
import com.githukudenis.intimo.core.util.MessageType
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.habit.R
import com.githukudenis.intimo.feature.habit.components.CountDownTimer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveHabitRoute(
    viewModel: HabitActiveViewModel = hiltViewModel(),
    onHabitCompleted: () -> Unit,
    onNavigateUp: () -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        LaunchedEffect(snackbarHostState, state.userMessages) {
            if (state.userMessages.isNotEmpty()) {
                val userMessage = state.userMessages.first()
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = userMessage.message ?: return@LaunchedEffect,
                    duration = when (userMessage.messageType) {
                        is MessageType.ERROR -> {
                            SnackbarDuration.Indefinite
                        }

                        MessageType.INFO -> SnackbarDuration.Short
                    }
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        //TODO Implement retry
                    }
                }
                viewModel.dismissMessage(userMessage.id)
            }
        }


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
                        habitData.habitName,
                        content = context.getString(R.string.habit_notification_description),
                        duration = state.timerState.currentTime,
                        id = habitData.habitId,
                        name = habitData.habitName
                    )
                },
                onPauseTimer = {
                    stopTimerService(context)
                },
                onResumeTimer = {
                    startTimerService(
                        context,
                        habitData.habitName,
                        content = context.getString(R.string.habit_notification_description),
                        duration = state.timerState.currentTime,
                        id = habitData.habitId,
                        name = habitData.habitName
                    )
                },
                onRestartHabit = {
                    viewModel.onRestartHabit()
                    stopTimerService(context)
                    startTimerService(
                        context,
                        habitData.habitName,
                        content = context.getString(R.string.habit_notification_description),
                        duration = habitData.duration,
                        id = habitData.habitId,
                        name = habitData.habitName
                    )

                },
                onCancelHabit = {
                    viewModel.onCancelHabit()
                    stopTimerService(context)
                    onNavigateUp()
                },
                onTimerFinished = {
                    viewModel.showUserMessage(
                        UserMessage(
                            message = context.getString(R.string.habit_completed),
                        )
                    )
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
                habitName = habit.habitName,
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
    name: String,
    duration: Long,
    id: Long
) {
    val intent = Intent(context, ActiveHabitService::class.java).apply {
        putExtra("title", title)
        putExtra("content", content)
        putExtra("duration", duration)
        putExtra("id", id)
        putExtra("name", name)
        action = ActiveHabitService.NotificationAction.START.toString()
    }
    context.startService(intent)
}