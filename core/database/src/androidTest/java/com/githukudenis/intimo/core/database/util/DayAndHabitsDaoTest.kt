package com.githukudenis.intimo.core.database.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.githukudenis.intimo.core.database.DayAndHabitsDao
import com.githukudenis.intimo.core.database.DayDao
import com.githukudenis.intimo.core.database.HabitDao
import com.githukudenis.intimo.core.database.IntimoDatabase
import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DayAndHabitCrossRef
import com.githukudenis.intimo.core.model.DayAndHabits
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.model.HabitType
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class DayAndHabitsDaoTest {

    @Inject
    @Named("test_db")
    lateinit var intimoDatabase: IntimoDatabase
    private lateinit var dayDao: DayDao
    private lateinit var dayAndHabitsDao: DayAndHabitsDao
    private lateinit var habitDao: HabitDao

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule by lazy { InstantTaskExecutorRule() }

    @Before
    fun setUp() {
        hiltRule.inject()
        dayDao = intimoDatabase.dayDao()
        dayAndHabitsDao = intimoDatabase.dayAndHabitsDao()
        habitDao = intimoDatabase.habitDao()
    }

    @After
    fun tearDown() {
        intimoDatabase.clearAllTables()
        intimoDatabase.close()
    }

    @Test
    fun insertDayAndHabits() = runTest {
        val day = Day(dayId = 100L)
        dayDao.insertDay(day)

        val habit = HabitData(
            habitId = 1,
            habitIcon = "",
            habitType = HabitType.READING,
            startTime = 18000000L,
            duration = 30000L,
            durationType = DurationType.MINUTE
        )

        habitDao.insertHabit(habit)
        val dayAndHabit = DayAndHabitCrossRef(
            dayId = day.dayId,
            habitId = habit.habitId
        )

        dayAndHabitsDao.insertDayWithHabit(dayAndHabit)

        val habits = dayAndHabitsDao.getDayAndHabits().first().map { it.day.dayId }
        assertThat(habits).contains(dayAndHabit.dayId)
    }

}