package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.database.IntimoHabitsDataSource
import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DayAndHabits
import com.githukudenis.intimo.core.model.DefaultHabit
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.RunningHabit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoHabitsRepository @Inject constructor(
    private val intimoHabitsDataSource: IntimoHabitsDataSource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : HabitsRepository {
    override val completedHabitList: Flow<List<DayAndHabits>>
        get() = intimoHabitsDataSource.dayAndHabits.flowOn(intimoCoroutineDispatcher.ioDispatcher)
    override val selectedHabitList: Flow<List<HabitData>>
        get() = intimoHabitsDataSource.activeHabits.flowOn(intimoCoroutineDispatcher.ioDispatcher)

    override suspend fun getHabitById(habitId: Long): HabitData {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.getHabitById(
                habitId
            )
        }
    }

    override val runningHabits: Flow<List<RunningHabit>>
        get() = intimoHabitsDataSource.runningHabits.flowOn(intimoCoroutineDispatcher.ioDispatcher)

    override suspend fun insertRunningHabit(habit: RunningHabit) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.insertRunningHabit(habit)
        }
    }

    override suspend fun updateRunningHabit(habit: RunningHabit) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.updateRunningHabit(habit)
        }
    }

    override suspend fun deleteRunningHabit(habit: RunningHabit) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoHabitsDataSource.deleteRunningHabit(habit)
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

    override val availableHabitList: Flow<List<DefaultHabit>>
        get() = intimoHabitsDataSource.provideDefaultHabits()
}