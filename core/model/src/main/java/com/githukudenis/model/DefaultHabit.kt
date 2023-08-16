package com.githukudenis.model

data class DefaultHabit(
    val icon: String,
    val habitType: HabitType,
    val selected: Boolean = false,
    val startTime: Long = 0,
    val duration: Long = 0
)

fun DefaultHabit.toHabitData(): HabitData {
    return HabitData(
        habitIcon = icon,
        habitType = habitType,
        startTime = startTime,
        duration = duration
    )
}

enum class DurationType {
    HOUR,
    MINUTE
}