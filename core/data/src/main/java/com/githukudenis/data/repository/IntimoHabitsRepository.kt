package com.githukudenis.data.repository

import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.database.IntimoHabitsDataSource
import com.githukudenis.model.Day
import com.githukudenis.model.DayAndHabits
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoHabitsRepository @Inject constructor(
    private val intimoHabitsDataSource: IntimoHabitsDataSource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : HabitsRepository {
    override val completedHabitList: Flow<List<DayAndHabits>>
        get() = intimoHabitsDataSource.getDayAndHabits()
    override val selectedHabitList: Flow<List<HabitData>>
        get() = intimoHabitsDataSource.getActiveHabits()

    override suspend fun getHabitById(habitId: Long): HabitData {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.getHabitById(
                habitId
            )
        }
    }

    override suspend fun insertDay(day: Day) {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.insertDay(day)
        }
    }

    override suspend fun insertHabit(vararg habitData: HabitData) {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.insertHabit(*habitData)
        }
    }

    override suspend fun updateHabit(habitData: HabitData) {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.updateHabit(habitData)
        }
    }

    override suspend fun completeHabit(dayId: Long, habitId: Long) {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.completeHabit(
                dayId,
                habitId
            )
        }
    }

    override val availableHabitList: List<DefaultHabit>
        get() = intimoHabitsDataSource.provideDefaultHabits()
}