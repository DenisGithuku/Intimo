package com.githukudenis.intimo.feature.summary.ui.home

import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import java.time.LocalDate

sealed interface SummaryUiEvent {
    data object Refresh : SummaryUiEvent
    data class ShowMessage(val error: UserMessage) : SummaryUiEvent
    data class DismissMessage(val messageId: Long) : SummaryUiEvent
    data class SelectDayOnHistory(val date: Date) : SummaryUiEvent
    data class PermissionChange(val permissionState: PermissionState): SummaryUiEvent
    data class ChangeHabitName(val habitName: String): SummaryUiEvent
    data class ChangeHabitStartTime(val startTime: Long): SummaryUiEvent
    data class ChangeHabitDuration(val duration: Long): SummaryUiEvent
    data class ChangeHabitFrequency(val habitFrequency: HabitFrequency): SummaryUiEvent
    data class ChangeHabitDays(val habitDays: List<LocalDate>): SummaryUiEvent
    data class ChangeRemindTime(val remindTime: Long): SummaryUiEvent
    data class ChangeHabitIcon(val icon: String): SummaryUiEvent
    data object SaveHabit: SummaryUiEvent

}