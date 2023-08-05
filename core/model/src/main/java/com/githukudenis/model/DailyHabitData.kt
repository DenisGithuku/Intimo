package com.githukudenis.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "DailyDataTable")
data class DailyData(
    @PrimaryKey(autoGenerate = true)
    var dailyId: Long
)

data class DailyDataWithHabits(
    @Embedded
    val dailyData: DailyData,
    @Relation(
        parentColumn = "dailyId",
        entityColumn = "dailyDataId"
    )
    val habitData: List<HabitData>
)


