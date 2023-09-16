package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.githukudenis.intimo.core.model.DayAndHabitCrossRef
import com.githukudenis.intimo.core.model.DayAndHabits
import kotlinx.coroutines.flow.Flow

@Dao
interface DayAndHabitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayWithHabit(dayAndHabitCrossRef: DayAndHabitCrossRef)

    @Transaction
    @Query("SELECT * FROM Day")
    fun getDayAndHabits(): Flow<List<DayAndHabits>>
}