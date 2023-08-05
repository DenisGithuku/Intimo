package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import com.githukudenis.model.DailyData
import com.githukudenis.model.DailyDataWithHabits
import com.githukudenis.model.HabitData
import kotlinx.coroutines.flow.Flow

@Database(entities = [DailyData::class, HabitData::class], version = 1, exportSchema = false)
@TypeConverters(value = [HabitTypeConverter::class])
abstract class IntimoDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}


@Dao
interface HabitDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabits(dailyData: DailyData, habitDataList: List<HabitData>)

    @Transaction
    @Query("SELECT * FROM DailyDataTable")
    fun getHabitList(): Flow<List<DailyDataWithHabits>>

    @Transaction
    @Update
    fun updateHabit(habitData: HabitData)

    @Transaction
    @Query("SELECT * FROM HabitTable WHERE habitDataId LIKE :habitId")
    fun getHabitById(habitId: Int): Flow<HabitData>
}