package com.githukudenis.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "HabitTable")
data class HabitData(
    @PrimaryKey(autoGenerate = true)
    var habitDataId: Int = 0,
    var dailyDataId: Long = 0L,
    val habitIcon: String,
    val habitType: HabitType,
    val habitPoints: Int = 0,
)

