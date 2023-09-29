package com.githukudenis.intimo.core.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Entity
data class HabitData(
    @PrimaryKey(autoGenerate = true) var habitId: Long = 0,
    val habitIcon: String,
    val habitName: String,
    val startTime: Long = 0,
    val duration: Long = 0,
    val durationType: DurationType,
    val habitFrequency: HabitFrequency = HabitFrequency.DAILY,
    val habitDays: List<LocalDate> = emptyList(),
    val remindTime: Long = 0L
)

class HabitFrequencyConverter {
    @TypeConverter
    fun toString(habitFrequency: HabitFrequency): String {
        return habitFrequency.name
    }

    @TypeConverter
    fun toHabitFrequency(value: String): HabitFrequency {
        return HabitFrequency.valueOf(value)
    }
}


class HabitDayListConverter {
    @TypeConverter
    fun toString(days: List<LocalDate>): String {
        return days.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<LocalDate> {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            value.split(",").map { LocalDate.parse(it, formatter)}
        } catch (t: Throwable) {
            Log.e("Date formatting", t.localizedMessage ?: "Could not convert date")
            emptyList()
        }
    }
}