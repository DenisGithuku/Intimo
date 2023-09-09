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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
            CenterAlignedTopAppBar(title = {  }, navigationIcon = {
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
                    viewModel.onToggleTimer(true)
                    startTimerService(
                        context,
                        habitData.habitType.nameToString(),
                        context.getString(R.string.habit_notification_description),
                        duration = state.timerState.currentTime ?: 0L,
                        habitId = habitData.habitId
                    )
                },
                onTimerFinished = {
                    viewModel.onToggleTimer(false)
                    currentOnHabitCompleted()
                },
                onPauseTimer = { remTime ->
                    viewModel.onToggleTimer(false)
                    stopTimerService(context)
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
    onTimerFinished: () -> Unit,
    onPauseTimer: (Long) -> Unit,
) {

    val (totalTime, currentTime) = activeHabitUiState.timerState

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        activeHabitUiState.habitData?.habitType?.let { habitType ->
            Text(
                text = habitType.nameToString(),
                style = MaterialTheme.typography.displayMedium
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
                        onStartTimer = onStartTimer,
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
    duration: Long,
    habitId: Long
) {
    val intent = Intent(context, ActiveHabitService::class.java).apply {
        putExtra("title", title)
        putExtra("content", content)
        putExtra("duration", duration)
        putExtra("habitId", habitId)
        action = ActiveHabitService.NotificationAction.START.toString()
    }
    context.startService(intent)
}