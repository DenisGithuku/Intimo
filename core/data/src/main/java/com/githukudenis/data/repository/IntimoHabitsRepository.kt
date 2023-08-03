package com.githukudenis.data.repository

import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.database.di.HabitDao
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoHabitsRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
): HabitsRepository {

    override suspend fun getHabitList(): Flow<List<HabitData>> {
        return flow {
            withContext(intimoCoroutineDispatcher.ioDispatcher) {
                habitDao.getHabitList()
            }
        }
    }

    override suspend fun getHabitById(habitId: Int): Flow<HabitData> {
        TODO("Not yet implemented")
    }

    override suspend fun updateHabit(habitData: HabitData) {
        TODO("Not yet implemented")
    }
}