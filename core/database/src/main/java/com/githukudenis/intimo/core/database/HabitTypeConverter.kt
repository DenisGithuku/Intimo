package com.githukudenis.intimo.core.database

import androidx.room.TypeConverter
import com.githukudenis.model.HabitType

class HabitTypeConverter {
    @TypeConverter
    fun toHabitType(habitString: String): HabitType {
        return HabitType.valueOf(habitString)
    }

    @TypeConverter
    fun toHabitString(habitType: HabitType): String {
        return habitType.name
    }
}
