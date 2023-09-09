package com.githukudenis.model

data class RunningHabit(
    val habitId: Long? = null,
    val isRunning: Boolean = false,
    val habitType: HabitType? = null,
    val totalTime: Long? = null,
    val remainingTime: Long? = null
)
