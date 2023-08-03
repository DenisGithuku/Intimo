package com.githukudenis.intimo.core.database

import com.githukudenis.intimo.core.database.di.HabitDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IntimoHabitsDataSource @Inject constructor(
    private val habitDao: HabitDao
) {
    suspend fun getHabitList(): Flow<List<HabitDBO>> {
        return flow {
            val habitList = habitDao.getHabitList()
            emit(habitList)
        }
    }

    suspend fun getHabitById(id: Int): Flow<HabitDBO> {
        return flow {
            val habitDBO = habitDao.getHabitById(id)
        }
    }

    suspend fun deleteHabit(habitId: Int) {
        habitDao.deleteHabit(habitId)
    }

    suspend fun updateHabit(habitDBO: HabitDBO) {
        habitDao.updateHabit(habitDBO)
    }

    suspend fun insertHabits(habits: List<HabitDBO>) {
        habitDao.insertHabits(habits)
    }
}