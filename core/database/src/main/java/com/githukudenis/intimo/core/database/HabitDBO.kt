package com.githukudenis.intimo.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType

@Entity(tableName = "HabitTable")
data class HabitDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val habitPoints: Int = 0,
)

fun HabitDBO.toHabitData(): HabitData {
    return HabitData(
        id = id,
        habitIcon = habitIcon,
        habitType = habitType,
        habitPoints = habitPoints
    )
}


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
