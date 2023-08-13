package com.githukudenis.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Day(
    @PrimaryKey
    val dayId: Long,
)

@Entity(primaryKeys = ["dayId", "habitId"])
data class DayAndHabitCrossRef(
    val dayId: Long,
    val habitId: Long
)

data class DayAndHabits(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "habitId",
        associateBy = Junction(
            DayAndHabitCrossRef::class
        )
    )
    val habits: List<HabitData>
)

data class HabitAndDay(
    @Embedded val habit: HabitData,
    @Relation(
        parentColumn = "habitId",
        entityColumn = "dayId",
        associateBy = Junction(
            value = DayAndHabitCrossRef::class
        )
    )
    val days: List<Day>
)
