package com.githukudenis.summary.ui.home

import com.githukudenis.summary.ui.UserMessage

sealed interface SummaryUiEvent {
    data object Refresh : SummaryUiEvent
    data class ShowMessage(val error: UserMessage) : SummaryUiEvent
    data class DismissMessage(val messageId: Long) : SummaryUiEvent
    data class EditHabit(val habitId: Long) : SummaryUiEvent
    data object UpdateHabit : SummaryUiEvent
    data class StartHabit(val habitId: Long): SummaryUiEvent
}