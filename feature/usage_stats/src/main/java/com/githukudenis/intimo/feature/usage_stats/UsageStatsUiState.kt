package com.githukudenis.intimo.feature.usage_stats

import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.util.UserMessage
import java.time.DayOfWeek
import java.time.LocalDate

sealed class UsageStatsUiState {
    data object Loading : UsageStatsUiState()
    data class Loaded(
        val usageStats: DataUsageStats = DataUsageStats(),
        val userMessages: List<UserMessage> = emptyList(),
        val appsInFocusMode: List<AppInFocusMode> = emptyList(),
        val chartState: ChartState = ChartState()
    ) : UsageStatsUiState()

    data class Error(val userMessageList: List<UserMessage> = emptyList()) : UsageStatsUiState()
}

data class ChartState(
    val selectedDate: LocalDate = LocalDate.now(),
    val data: Map<LocalDate, Pair<Float, Long>> = emptyMap()
)