package com.githukudenis.data.repository

import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.database.IntimoHabitsDataSource
import com.githukudenis.model.DailyData
import com.githukudenis.model.DailyDataWithHabits
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoHabitsRepository @Inject constructor(
    private val intimoHabitsDataSource: IntimoHabitsDataSource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : HabitsRepository {

    override suspend fun getHabitList(): Flow<List<DailyDataWithHabits>> {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.getHabitList()
        }
    }

    override suspend fun getHabitById(habitId: Int): Flow<HabitData> {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.getHabitById(habitId)
        }
    }

    override suspend fun updateHabit(habitData: HabitData) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.updateHabit(habitData)
        }
    }

    override suspend fun insertHabits(dailyData: DailyData, habitDataList: List<HabitData>) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.insertHabits(dailyData, habitDataList)
        }
    }
}