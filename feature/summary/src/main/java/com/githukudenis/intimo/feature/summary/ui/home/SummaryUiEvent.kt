package com.githukudenis.intimo.feature.summary.ui.home

import com.githukudenis.intimo.core.util.UserMessage

sealed interface SummaryUiEvent {
    data object Refresh : SummaryUiEvent
    data class ShowMessage(val error: UserMessage) : SummaryUiEvent
    data class DismissMessage(val messageId: Long) : SummaryUiEvent
}