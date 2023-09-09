package com.githukudenis.model

enum class HabitType {
    MEDITATION,
    READING,
    JOURNALING,
    REFLECTION,
    EXERCISE,
    STRETCHING,
    DECLUTTERRING,
    FLOSSING,
    BREATHING
}

fun HabitType.nameToString(): String {
    return name.lowercase().replaceFirstChar { it.uppercase() }
}