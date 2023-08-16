package com.githukudenis.summary.ui

import com.githukudenis.model.Day
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType

data class SummaryUiState(
    val isLoading: Boolean = false,
    val summaryData: SummaryData? = null,
    val notificationCount: Long = 0L,
    val days: List<Day> = emptyList(),
    val habitDataList: List<HabitUiModel> = emptyList(),
    val completedHabits: List<HabitData> = emptyList(),
    val userErrorList: List<UserError> = emptyList()
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