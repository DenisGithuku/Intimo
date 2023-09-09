package com.githukudenis.intimo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabitCrossRef
import com.githukudenis.model.HabitData

@Database(
    entities = [
        Day::class,
        HabitData::class,
        DayAndHabitCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [HabitTypeConverter::class, DurationTypeConverter::class])
abstract class IntimoDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    abstract fun dayDao(): DayDao

    abstract fun dayAndHabitsDao(): DayAndHabitsDao
}


