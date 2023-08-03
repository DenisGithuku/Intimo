package com.githukudenis.model

data class HabitData(
    val id: Int = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val habitPoints: Int = 0,
)
