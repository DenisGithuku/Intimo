package com.githukudenis.intimo.feature.summary.ui.home

import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance


data class SummaryUiState(
    val isLoading: Boolean = false,
    val usageStatsState: UsageStatsState = UsageStatsState.Loading,
    val habitsState: HabitsState = HabitsState.Loading,
    val userMessageList: List<UserMessage> = emptyList(),
)

sealed class UsageStatsState {
    data object Loading : UsageStatsState()
    data class Loaded(
        val usageStats: List<ApplicationInfoData>,
        val unlockCount: Int = 0,
        val notificationCount: Int = 0
    ) : UsageStatsState()

    data object Empty : UsageStatsState()

}

sealed class HabitsState {
    data object Loading : HabitsState()
    data class Success(
        val runningHabitState: RunningHabitState = RunningHabitState(),
        val habitDataList: List<HabitUiModel> = emptyList(),
        val habitHistoryStateList: Map<Date, Float> = emptyMap(),
        val habitPerformance: HabitPerformance = HabitPerformance.GOOD
    ) : HabitsState()

    data object Empty : HabitsState()
}

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

