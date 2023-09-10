package com.githukudenis.summary.ui.home

import com.githukudenis.summary.ui.UserMessage

sealed interface SummaryUiEvent {
    data object Refresh : SummaryUiEvent
    data class ShowMessage(val error: UserMessage) : SummaryUiEvent
    data class DismissMessage(val messageId: Long) : SummaryUiEvent
}