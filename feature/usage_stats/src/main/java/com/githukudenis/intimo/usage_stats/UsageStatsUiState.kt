package com.githukudenis.intimo.usage_stats

import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.model.DataUsageStats

sealed class UsageStatsUiState {
    data object Loading: UsageStatsUiState()
    data class Loaded(val usageStats: DataUsageStats = DataUsageStats()): UsageStatsUiState()
    data class Error(val userMessageList: List<UserMessage> = emptyList()): UsageStatsUiState()
}



