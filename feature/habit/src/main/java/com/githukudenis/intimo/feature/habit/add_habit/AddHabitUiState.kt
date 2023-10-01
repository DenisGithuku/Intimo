package com.githukudenis.intimo.feature.habit.add_habit

import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.habit.detail.getDaysInAWeek
import java.time.LocalDate

data class AddHabitUiState(
    val habitName: String = "",
    val habitIcon: String = "😊",
    val startTime: Long = 0L,
    val habitDuration: Long = 0L,
    val habitDurationType: DurationType = DurationType.MINUTE,
    val habitFrequency: HabitFrequency = HabitFrequency.DAILY,
    val days: List<LocalDate> = getDaysInAWeek(),
    val remindTime: Long = 0L,
    val userMessages: List<UserMessage> = emptyList()
)