package com.githukudenis.intimo.core.database

import com.githukudenis.model.DailyData
import com.githukudenis.model.DailyDataWithHabits
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IntimoHabitsDataSource @Inject constructor(
    private val habitDao: HabitDao
) {
    suspend fun getHabitList(): Flow<List<DailyDataWithHabits>> {
        return habitDao.getHabitList()
    }

    suspend fun getHabitById(id: Int): Flow<HabitData> {
        return habitDao.getHabitById(id)
    }

    suspend fun updateHabit(habitData: HabitData) {
        habitDao.updateHabit(habitData)
    }

    suspend fun insertHabits(dailyData: DailyData, habitData: List<HabitData>) {
        habitDao.insertHabits(dailyData, habitData)
    }
}