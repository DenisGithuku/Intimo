package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DayAndHabits
import com.githukudenis.intimo.core.model.DefaultHabit
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.RunningHabit
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