package com.githukudenis.intimo.core.database.di

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import com.githukudenis.intimo.core.database.HabitDBO
import com.githukudenis.intimo.core.database.HabitTypeConverter

@Database(entities = [], version = 1, exportSchema = false)
@TypeConverters(value = [HabitTypeConverter::class])
abstract class IntimoDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao
}


@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabits(habits: List<HabitDBO>)

    @Query("SELECT * FROM HabitTable")
    fun getHabitList(): List<HabitDBO>

    @Query("DELETE FROM HabitTable WHERE id LIKE :habitId")
    fun deleteHabit(habitId: Int)

    @Update
    fun updateHabit(habitDBO: HabitDBO)

    @Query("SELECT * FROM HabitTable WHERE id LIKE :habitId")
    fun getHabitById(habitId: Int): HabitDBO
}