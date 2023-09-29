package com.githukudenis.intimo.core.model

import java.time.LocalDate

data class DefaultHabit(
    val icon: String,
    val habitName: String,
    val selected: Boolean = false,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType,
    val habitFrequency: HabitFrequency,
    val habitDays: List<LocalDate>
)

fun DefaultHabit.toHabitData(): HabitData {
    return HabitData(
        habitIcon = icon,
        habitName = habitName,
        startTime = startTime,
        duration = duration,
        durationType = durationType
    )
}

enum class DurationType {
    HOUR,
    MINUTE
}