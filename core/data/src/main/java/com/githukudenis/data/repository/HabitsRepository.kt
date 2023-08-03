package com.githukudenis.data.repository

import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow

interface HabitsRepository {

    suspend fun getHabitList(): Flow<List<HabitData>>

    suspend fun getHabitById(habitId: Int): Flow<HabitData>

    suspend fun updateHabit(habitData: HabitData)
}