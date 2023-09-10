package com.githukudenis.intimo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabitCrossRef
import com.githukudenis.model.DayAndNotificationsPostedCrossRef
import com.githukudenis.model.HabitData
import com.githukudenis.model.NotificationPosted
import com.githukudenis.model.RunningHabit

@Database(
    entities = [
        Day::class,
        HabitData::class,
        NotificationPosted::class,
        DayAndHabitCrossRef::class,
        DayAndNotificationsPostedCrossRef::class,
        RunningHabit::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [HabitTypeConverter::class, DurationTypeConverter::class])
abstract class IntimoDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    abstract fun dayDao(): DayDao

    abstract fun dayAndHabitsDao(): DayAndHabitsDao

    abstract fun dayAndNotificationsDao(): DayAndNotificationsDao

    abstract fun notificationsDao(): NotificationsDao
}


