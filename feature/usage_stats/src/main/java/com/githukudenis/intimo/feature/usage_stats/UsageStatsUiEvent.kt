package com.githukudenis.intimo.feature.usage_stats

import com.githukudenis.intimo.core.util.UserMessage

sealed class UsageStatsUiEvent {
    data class LimitApp(val packageName: String, val limitDuration: Long): UsageStatsUiEvent()
    data class ShowUserMessage(val userMessage: UserMessage): UsageStatsUiEvent()
    data class DismissUserMessage(val messageId: Long): UsageStatsUiEvent()
}