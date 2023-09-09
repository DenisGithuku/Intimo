package com.githukudenis.intimo.habit.active

import com.githukudenis.model.HabitData

data class ActiveHabitUiState(
    val habitId: Long? = null,
    val habitData: HabitData? = null,
    val timerState: TimerState = TimerState()
)


data class TimerState(
    val totalTime: Long? = null,
    val currentTime: Long? = null,
    val isRunning: Boolean = false
) {
    val timerButtonStatusText: String?
        get() {
            return currentTime?.let { currTime ->
                totalTime?.let { totalT ->
                   if (currTime == totalT && !isRunning) {
                       "Start"
                   } else if (currTime in 1..<totalT && !isRunning) {
                       "Resume"
                   } else {
                       "Pause"
                   }
                }
            }
        }
}