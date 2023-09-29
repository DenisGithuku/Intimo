package com.githukudenis.intimo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DayAndHabitCrossRef
import com.githukudenis.intimo.core.model.DayAndNotificationsPostedCrossRef
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.HabitFrequencyConverter
import com.githukudenis.intimo.core.model.HabitDayListConverter
import com.githukudenis.intimo.core.model.NotificationPosted
import com.githukudenis.intimo.core.model.RunningHabit

@Database(
    entities = [
        Day::class,
        HabitData::class,
        AppInFocusMode::class,
        NotificationPosted::class,
        DayAndHabitCrossRef::class,
        DayAndNotificationsPostedCrossRef::class,
        RunningHabit::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [DurationTypeConverter::class, HabitDayListConverter::class, HabitFrequencyConverter::class])
abstract class IntimoDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    abstract fun dayDao(): DayDao

    abstract fun dayAndHabitsDao(): DayAndHabitsDao

    abstract fun dayAndNotificationsDao(): DayAndNotificationsDao

    abstract fun notificationsDao(): NotificationsDao

    abstract fun appsInFocusModeDao(): AppsInFocusModeDao
}


