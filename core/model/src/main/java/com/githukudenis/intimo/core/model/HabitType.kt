package com.githukudenis.intimo.core.model

enum class HabitType {
    MEDITATION,
    READING,
    JOURNALING,
    REFLECTION,
    EXERCISE,
    STRETCHING,
    NAP,
    FLOSSING,
    BREATHING
}

fun HabitType.nameToString(): String {
    return name.lowercase().replaceFirstChar { it.uppercase() }
}