package com.githukudenis.data.repository

import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabits
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.HabitData
import com.githukudenis.model.RunningHabit
import kotlinx.coroutines.flow.Flow

interface HabitsRepository {

    val completedHabitList: Flow<List<DayAndHabits>>

    val selectedHabitList: Flow<List<HabitData>>

    val availableHabitList: Flow<List<DefaultHabit>>

    val runningHabits: Flow<List<RunningHabit>>
    suspend fun getHabitById(habitId: Long): HabitData
    suspend fun insertDay(day: Day)
    suspend fun insertHabit(vararg habitData: HabitData)
    suspend fun updateHabit(habitData: HabitData)
    suspend fun completeHabit(dayId: Long, habitId: Long)
    suspend fun insertRunningHabit(habit: RunningHabit)
    suspend fun updateRunningHabit(habit: RunningHabit)
    suspend fun deleteRunningHabit(habit: RunningHabit)
}