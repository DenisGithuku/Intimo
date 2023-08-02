package com.githukudenis.intimo.core.database

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.githukudenis.model.HabitType

@Entity(tableName = "HabitTable")
data class HabitDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitIcon: String,
    val habitType: HabitType,
    val habitPoints: Int = 0,
)


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
