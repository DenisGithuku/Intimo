package com.githukudenis.intimo.core.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Day(
    @PrimaryKey
    val dayId: Long,
)

@Entity(primaryKeys = ["dayId", "habitId"], indices = [Index("habitId")])
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