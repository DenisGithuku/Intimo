package com.githukudenis.summary.ui.home

import com.githukudenis.model.Day
import com.githukudenis.model.DurationType
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType
import com.githukudenis.summary.ui.UserMessage



data class SummaryUiState(
    val isLoading: Boolean = false,
    val summaryData: SummaryData? = null,
    val notificationCount: Long = 0L,
    val days: List<Day> = emptyList(),
    val habitInEditModeState: HabitInEditModeState = HabitInEditModeState(),
    val habitDataList: List<HabitUiModel> = emptyList(),
    val userMessageList: List<UserMessage> = emptyList()
)

data class HabitInEditModeState(
    val habitModel: HabitUiModel? = null
)

data class HabitUiModel(
    val completed: Boolean = false,
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