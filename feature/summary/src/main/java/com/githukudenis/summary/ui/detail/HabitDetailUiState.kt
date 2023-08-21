package com.githukudenis.summary.ui.detail

import com.githukudenis.summary.ui.home.HabitUiModel
import java.time.LocalDate

data class HabitDetailUiState(
    val isLoading: Boolean = false,
    val habitId: Long? = null,
    val habitUiModel: HabitUiModel? = null,
    val selectedDate: Long? = null,
    val completedDayList: List<LocalDate> = emptyList()
)