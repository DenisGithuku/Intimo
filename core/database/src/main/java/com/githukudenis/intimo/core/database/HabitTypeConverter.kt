package com.githukudenis.intimo.core.database

import androidx.room.TypeConverter
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType

class HabitTypeConverter {
    @TypeConverter
    fun toHabitType(habitString: String): HabitType = HabitType.valueOf(habitString)

    @TypeConverter
    fun toHabitString(habitType: HabitType): String = habitType.name

}

class DurationTypeConverter {
    @TypeConverter
    fun toDurationType(durationString: String): DurationType = DurationType.valueOf(durationString)


    @TypeConverter
    fun toDurationString(durationType: DurationType): String = durationType.name
}
