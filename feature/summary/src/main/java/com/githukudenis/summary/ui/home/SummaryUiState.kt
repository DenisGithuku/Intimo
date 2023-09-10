package com.githukudenis.summary.ui.home

import com.githukudenis.model.Day
import com.githukudenis.model.DurationType
import com.githukudenis.model.HabitType
import com.githukudenis.intimo.core.util.UserMessage


data class SummaryUiState(
    val isLoading: Boolean = false,
    val summaryData: SummaryData? = null,
    val notificationCount: Int = 0,
    val runningHabitState: RunningHabitState = RunningHabitState(),
    val days: List<Day> = emptyList(),
    val habitDataList: List<HabitUiModel> = emptyList(),
    val userMessageList: List<UserMessage> = emptyList()
)

data class HabitUiModel(
    val completed: Boolean = false,
    val remainingTime: Long = 0L,
    val habitId: Long = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType
)

data class RunningHabitState(
    val habitId: Long? = null,
    val isRunning: Boolean = false,
    val remainingTime: Long = 0L
)

