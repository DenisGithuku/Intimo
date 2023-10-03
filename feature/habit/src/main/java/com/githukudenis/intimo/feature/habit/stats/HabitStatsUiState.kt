package com.githukudenis.intimo.feature.habit.stats

import com.githukudenis.intimo.core.model.HabitData
import java.time.LocalDate

data class HabitStatsUiState(
    val selectedStatType: StatType = StatType.DAILY,
    val completionRateList: List<CompletionRate> = emptyList()
)

data class CompletionRate(
    val day: LocalDate = LocalDate.MIN,
    val completionPercentage: Int = 0,
)

enum class StatType {
    DAILY,
    WEEKLY
}