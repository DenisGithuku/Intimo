package com.githukudenis.intimo.feature.habit.active

import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.util.UserMessage

data class ActiveHabitUiState(
    val habitId: Long? = null,
    val habitData: HabitData? = null,
    val timerState: TimerState = TimerState(),
    val userMessages: List<UserMessage> = emptyList()
)


data class TimerState(
    val totalTime: Long = 0L,
    val currentTime: Long = 0L,
    val isRunning: Boolean = false
)