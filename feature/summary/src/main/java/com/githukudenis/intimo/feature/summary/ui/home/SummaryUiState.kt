package com.githukudenis.intimo.feature.summary.ui.home

import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance


data class SummaryUiState(
    val isLoading: Boolean = false,
    val summaryData: SummaryData? = null,
    val notificationCount: Int = 0,
    val runningHabitState: RunningHabitState = RunningHabitState(),
    val days: List<Day> = emptyList(),
    val habitDataList: List<HabitUiModel> = emptyList(),
    val userMessageList: List<UserMessage> = emptyList(),
    val habitHistoryStateList: Map<Date, Float> = emptyMap(),
    val habitPerformance: HabitPerformance = HabitPerformance.GOOD
)

data class HabitUiModel(
    val completed: Pair<Long, Boolean> = Pair(0L, false),
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

