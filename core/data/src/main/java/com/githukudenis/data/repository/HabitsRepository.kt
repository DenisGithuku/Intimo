package com.githukudenis.data.repository

import com.githukudenis.model.DailyData
import com.githukudenis.model.DailyDataWithHabits
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow

interface HabitsRepository {

    suspend fun insertHabits(dailyData: DailyData, habitDataList: List<HabitData>)

    suspend fun getHabitList(): Flow<List<DailyDataWithHabits>>

    suspend fun getHabitById(habitId: Int): Flow<HabitData>

    suspend fun updateHabit(habitData: HabitData)
}