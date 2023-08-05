package com.githukudenis.model

enum class HabitType {
    MEDITATION,
    READING,
    JOURNALING,
    SLEEP,
    REFLECTION,
    EXERCISE,
    STRETCHING,
    NUTRITION
}

fun HabitType.nameToString(): String {
    return name.lowercase().replaceFirstChar { it.uppercase() }
}