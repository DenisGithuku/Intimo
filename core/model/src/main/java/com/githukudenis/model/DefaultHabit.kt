package com.githukudenis.model

data class DefaultHabit(
    val icon: String,
    val habitType: HabitType,
    val selected: Boolean = false,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType
)

fun DefaultHabit.toHabitData(): HabitData {
    return HabitData(
        habitIcon = icon,
        habitType = habitType,
        startTime = startTime,
        duration = duration,
        durationType = durationType
    )
}

enum class DurationType {
    HOUR,
    MINUTE
}