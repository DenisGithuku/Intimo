package com.githukudenis.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class HabitData(
    @PrimaryKey(autoGenerate = true)
    var habitId: Long = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val startTime: Long = 0,
    val duration: Long = 0,
)

