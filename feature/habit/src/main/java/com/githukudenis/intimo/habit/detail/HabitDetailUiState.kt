package com.githukudenis.intimo.habit.detail

import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType
import java.time.LocalDate

data class HabitDetailUiState(
    val isLoading: Boolean = false,
    val habitId: Long? = null,
    val habitUiModel: HabitUiModel? = null,
    val selectedDate: Long? = null,
    val completedDayList: List<LocalDate> = emptyList()
)

data class HabitUiModel(
    val completed: Boolean = false,
    val habitId: Long = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val startTime: Long = 0,
    val duration: Long = 0
)

fun HabitData.toHabitUiModel(completed: Boolean): HabitUiModel {
    return HabitUiModel(
        completed = completed,
        habitId = habitId,
        habitIcon = habitIcon,
        habitType = habitType,
        startTime = startTime,
        duration = duration
    )
}