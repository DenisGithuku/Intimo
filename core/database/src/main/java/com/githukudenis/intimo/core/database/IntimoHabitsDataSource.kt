package com.githukudenis.intimo.core.database

import android.content.Context
import android.text.format.DateFormat
import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabitCrossRef
import com.githukudenis.model.DayAndHabits
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.DurationType
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class IntimoHabitsDataSource @Inject constructor(
    private val habitDao: HabitDao,
    private val dayDao: DayDao,
    private val dayAndHabitsDao: DayAndHabitsDao,
    private val context: Context
) {

    fun insertDay(day: Day) {
        dayDao.insertDay(day)
    }

    fun insertHabit(vararg habitData: HabitData) {
        habitDao.insertHabit(*habitData)
    }

    fun updateHabit(habitData: HabitData) {
        habitDao.updateHabit(habitData)
    }

    fun getDayAndHabits(): Flow<List<DayAndHabits>> {
        return dayAndHabitsDao.getDayAndHabits()
    }


    fun getHabitById(habitId: Long): HabitData {
        return habitDao.getHabit(habitId)
    }

    fun getActiveHabits(): Flow<List<HabitData>> {
        return habitDao.getHabitList()
    }

    fun completeHabit(dayId: Long, habitId: Long) {
        dayAndHabitsDao.insertDayWithHabit(
            DayAndHabitCrossRef(
                dayId = dayId,
                habitId = habitId
            )
        )
    }

    fun provideDefaultHabits(): List<DefaultHabit> {
        val defaultHabitList = listOf(
            DefaultHabit(
                icon = "\uD83D\uDCDA",
                habitType = HabitType.READING,
                startTime = generateHabitTime(hour = 20),
                duration = generateDuration(1, DurationType.HOUR)
            ),
            DefaultHabit(
                icon = "\uD83E\uDDD8",
                habitType = HabitType.MEDITATION,
                startTime = generateHabitTime(hour = 7),
                duration = generateDuration(15, DurationType.MINUTE)
            ),
            DefaultHabit(
                icon = "\uD83D\uDECC",
                habitType = HabitType.SLEEP,
                startTime = generateHabitTime(21),
                duration = generateDuration(8, DurationType.HOUR),
            ),
            DefaultHabit(
                icon = "✍️",
                habitType = HabitType.JOURNALING,
                startTime = generateHabitTime(19),
                duration = generateDuration(30, DurationType.MINUTE)
            ),
            DefaultHabit(
                icon = "\uD83C\uDFC3",
                habitType = HabitType.EXERCISE,
                startTime = generateHabitTime(6),
                duration = generateDuration(1, DurationType.HOUR)
            ),
            DefaultHabit(
                icon = "\uD83E\uDD14",
                habitType = HabitType.REFLECTION,
                startTime = generateHabitTime(2),
                duration = generateDuration(15, DurationType.MINUTE)
            ),
            DefaultHabit(
                icon = "\uD83C\uDF4E",
                habitType = HabitType.NUTRITION,
            ),
            DefaultHabit(
                icon = "\uD83E\uDD38",
                habitType = HabitType.STRETCHING,
                startTime = generateHabitTime(11),
                duration = generateDuration(10, DurationType.MINUTE)
            )
        )
        return defaultHabitList
    }

    private fun generateHabitTime(hour: Int): Long {
        val calendar = Calendar.getInstance()

        val isIn24HourFormat = DateFormat.is24HourFormat(context)
        calendar.apply {
            if (isIn24HourFormat) {
                set(Calendar.HOUR_OF_DAY, hour)
            } else {
                set(Calendar.HOUR, hour - 12)
            }
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun generateDuration(value: Int, durationType: DurationType): Long {
        val hour = 3_600_000L
        val minute = 60_000L

        return when (durationType) {
            DurationType.HOUR -> {
                hour * value
            }

            DurationType.MINUTE -> {
                value * minute
            }
        }
    }
}