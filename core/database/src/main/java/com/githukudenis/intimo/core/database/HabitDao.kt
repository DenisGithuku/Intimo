package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(vararg habitData: HabitData)

    @Query("SELECT * FROM HabitData")
    fun getHabitList(): Flow<List<HabitData>>

    @Update
    fun updateHabit(habitData: HabitData)

    @Query("SELECT * FROM HabitData WHERE habitId LIKE :id")
    fun getHabit(id: Long): HabitData
}