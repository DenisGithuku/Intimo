package com.githukudenis.intimo.habit.active

import com.githukudenis.model.HabitData

data class ActiveHabitUiState(
    val habitId: Long? = null,
    val habitData: HabitData? = null,
    val timerState: TimerState = TimerState()
)


data class TimerState(
    val totalTime: Long = 0L,
    val currentTime: Long = 0L,
    val isRunning: Boolean = false
)