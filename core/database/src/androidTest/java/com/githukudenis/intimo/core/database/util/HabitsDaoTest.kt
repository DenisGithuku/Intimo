package com.githukudenis.intimo.core.database.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.githukudenis.intimo.core.database.HabitDao
import com.githukudenis.intimo.core.database.IntimoDatabase
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.HabitType
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitsDaoTest {

    @Inject
    @Named("test_db")
    lateinit var intimoDatabase: IntimoDatabase
    private lateinit var habitDao: HabitDao

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule by lazy { InstantTaskExecutorRule() }

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        habitDao = intimoDatabase.habitDao()
    }

    @After
    fun tearDown() {
        intimoDatabase.clearAllTables()
        intimoDatabase.close()
    }

    @Test
    fun insertHabit() = runTest {
        val habit = HabitData(
            habitId = 1,
            habitIcon = "",
            habitType = HabitType.READING,
            startTime = 18000000L,
            duration = 30000L,
            durationType = DurationType.MINUTE
        )
        habitDao.insertHabit(habit)

        val habits = habitDao.getHabitList().first()
        assertThat(habits).contains(habit)
    }

    @Test
    fun updateHabit() = runTest {
        val habit = HabitData(
            habitId = 1,
            habitIcon = "",
            habitType = HabitType.READING,
            startTime = 18000000L,
            duration = 30000L,
            durationType = DurationType.MINUTE
        )
        habitDao.insertHabit(habit)
        val updatedHabit = habit.copy(habitType = HabitType.EXERCISE)
        habitDao.updateHabit(updatedHabit)
        val insertedHabit = habitDao.getHabitList().first().first { it.habitId == 1L }
        assertThat(insertedHabit.habitType).isEqualTo(HabitType.EXERCISE)
    }

    @Test
    fun getHabitById() = runTest {
        val habits =
            listOf(
                HabitData(
                    habitId = 1,
                    habitIcon = "",
                    habitType = HabitType.READING,
                    startTime = 18000000L,
                    duration = 30000L,
                    durationType = DurationType.MINUTE
                ),HabitData(
                    habitId = 2,
                    habitIcon = "",
                    habitType = HabitType.EXERCISE,
                    startTime = 18000000L,
                    duration = 30000L,
                    durationType = DurationType.MINUTE
                ),HabitData(
                    habitId = 3,
                    habitIcon = "",
                    habitType = HabitType.STRETCHING,
                    startTime = 18000000L,
                    duration = 30000L,
                    durationType = DurationType.MINUTE
                ),
            )
        habitDao.insertHabit(*habits.toTypedArray())
        val habitById = habitDao.getHabit(3)
        assertThat(habitById).isNotNull()
        assertThat(habitById).isEqualTo(habits[2])
    }

    @Test
    fun insertRunningHabit() = runTest {

    }
}