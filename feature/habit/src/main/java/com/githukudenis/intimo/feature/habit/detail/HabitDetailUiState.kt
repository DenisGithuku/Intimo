package com.githukudenis.intimo.feature.habit.detail

import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.HabitFrequency
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
    val habitName: String,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val selectedDays: List<LocalDate> = getDaysInAWeek(),
    val remindBefore: Long = 0L,
)

fun HabitData.toHabitUiModel(completed: Boolean): HabitUiModel {
    return HabitUiModel(
        completed = completed,
        habitId = habitId,
        habitIcon = habitIcon,
        habitName = habitName,
        startTime = startTime,
        duration = duration,
        durationType = durationType,
        frequency = habitFrequency,
        selectedDays = habitDays,
        remindBefore = remindTime
    )
}

fun HabitUiModel.toHabitData(): HabitData {
    return HabitData(
        habitId = habitId,
        habitIcon = habitIcon,
        habitName = habitName,
        startTime = startTime,
        duration = duration,
        durationType = durationType,
        habitFrequency = frequency,
        habitDays = selectedDays,
        remindTime = remindBefore
    )
}