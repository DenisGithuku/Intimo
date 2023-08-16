package com.githukudenis.summary.ui.detail

import com.githukudenis.model.HabitData
import com.githukudenis.summary.ui.HabitUiModel

data class HabitDetailUiState(
    val isLoading: Boolean = false,
    val habitUiModel: HabitUiModel? = null,
    val completedHabitList: List<HabitData> = emptyList()
)