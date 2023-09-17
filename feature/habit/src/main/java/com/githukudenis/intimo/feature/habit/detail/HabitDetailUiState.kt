package com.githukudenis.intimo.feature.habit.detail

import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.HabitType
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
    val running: Boolean = false,
    val habitId: Long = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType
)

fun HabitData.toHabitUiModel(completed: Boolean): HabitUiModel {
    return HabitUiModel(
        completed = completed,
        habitId = habitId,
        habitIcon = habitIcon,
        habitType = habitType,
        startTime = startTime,
        duration = duration,
        durationType = durationType
    )
}

fun HabitUiModel.toHabitData(): HabitData {
    return HabitData(
        habitId = habitId,
        habitIcon = habitIcon,
        habitType = habitType,
        startTime = startTime,
        duration = duration,
        durationType = durationType
    )
}