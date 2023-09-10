package com.githukudenis.intimo.core.database

import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabitCrossRef
import com.githukudenis.model.DayAndHabits
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.DurationType
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType
import com.githukudenis.model.RunningHabit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class IntimoHabitsDataSource @Inject constructor(
    private val habitDao: HabitDao,
    private val dayDao: DayDao,
    private val dayAndHabitsDao: DayAndHabitsDao,
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

    val dayAndHabits: Flow<List<DayAndHabits>> = dayAndHabitsDao.getDayAndHabits()

    fun insertRunningHabit(habit: RunningHabit) {
        habitDao.insertRunningHabit(habit)
    }

    val runningHabits: Flow<List<RunningHabit>> = habitDao.getRunningHabits()

    fun getHabitById(habitId: Long): HabitData {
        return habitDao.getHabit(habitId)
    }

    val activeHabits: Flow<List<HabitData>> = habitDao.getHabitList()

    fun completeHabit(dayId: Long, habitId: Long) {
        dayAndHabitsDao.insertDayWithHabit(
            DayAndHabitCrossRef(
                dayId = dayId,
                habitId = habitId
            )
        )
    }

    fun provideDefaultHabits(): Flow<List<DefaultHabit>> = flow {
        emit(listOf(
            DefaultHabit(
                icon = "\uD83D\uDCDA",
                habitType = HabitType.READING,
                startTime = generateHabitTime(hour = 20),
                duration = generateDuration(1, DurationType.HOUR),
                durationType = DurationType.HOUR
            ),
            DefaultHabit(
                icon = "\uD83E\uDDD8",
                habitType = HabitType.MEDITATION,
                startTime = generateHabitTime(7),
                duration = generateDuration(15, DurationType.MINUTE),
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "\uD83E\uDEA5",
                habitType = HabitType.FLOSSING,
                startTime = generateHabitTime(hour = 20, minute = 45),
                duration = generateDuration(3, DurationType.MINUTE),
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "✍️",
                habitType = HabitType.JOURNALING,
                startTime = generateHabitTime(19),
                duration = generateDuration(30, DurationType.MINUTE),
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "\uD83C\uDFC3",
                habitType = HabitType.EXERCISE,
                startTime = generateHabitTime(6),
                duration = generateDuration(1, DurationType.HOUR),
                durationType = DurationType.HOUR
            ),
            DefaultHabit(
                icon = "\uD83E\uDD14",
                habitType = HabitType.REFLECTION,
                startTime = generateHabitTime(14),
                duration = generateDuration(15, DurationType.MINUTE),
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "\uD83D\uDDD1️",
                startTime = generateHabitTime(8),
                duration = generateDuration(2, DurationType.HOUR),
                habitType = HabitType.DECLUTTERRING,
                durationType = DurationType.HOUR

            ),
            DefaultHabit(
                icon = "\uD83E\uDD38",
                habitType = HabitType.STRETCHING,
                startTime = generateHabitTime(11),
                duration = generateDuration(10, DurationType.MINUTE),
                durationType = DurationType.MINUTE

            )
        ))
    }

    private fun generateHabitTime(hour: Int, minute: Int = 0): Long {
        val calendar = Calendar.getInstance()

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
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

    fun updateRunningHabit(habit: RunningHabit) {
        habitDao.updateRunningHabit(habit)
    }

    fun deleteRunningHabit(habit: RunningHabit) {
        habitDao.deleteRunningHabit(habit)
    }
}