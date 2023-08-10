package com.githukudenis.summary.ui

sealed interface SummaryUiEvent {
    object Refresh : SummaryUiEvent
    data class ShowError(val error: UserError) : SummaryUiEvent
    data class CheckHabit(val habitId: Int) : SummaryUiEvent
}