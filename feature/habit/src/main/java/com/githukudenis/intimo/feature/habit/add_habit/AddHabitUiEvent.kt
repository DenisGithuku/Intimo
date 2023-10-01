package com.githukudenis.intimo.feature.habit.add_habit

import com.githukudenis.intimo.core.model.HabitFrequency
import java.time.LocalDate

sealed interface AddHabitUiEvent {
    data class ChangeHabitName(val habitName: String): AddHabitUiEvent
    data class ChangeHabitStartTime(val startTime: Long): AddHabitUiEvent
    data class ChangeHabitDuration(val duration: Long): AddHabitUiEvent
    data class ChangeHabitFrequency(val habitFrequency: HabitFrequency): AddHabitUiEvent
    data class ChangeHabitDays(val habitDays: List<LocalDate>): AddHabitUiEvent
    data class ChangeRemindTime(val remindTime: Long): AddHabitUiEvent
    data class ChangeHabitIcon(val icon: String): AddHabitUiEvent
    data class ShowUserMessage(val message: String): AddHabitUiEvent
    data class DismissUserMessage(val id: Long): AddHabitUiEvent
    data object SaveHabit: AddHabitUiEvent
}