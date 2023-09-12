package com.githukudenis.intimo.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class RunningHabit(
    @PrimaryKey
    val habitId: Long,
    val isRunning: Boolean = false,
    val habitType: HabitType,
    val totalTime: Long = 0L,
    val remainingTime: Long = 0L
)
