package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.githukudenis.model.Day

@Dao
interface DayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDay(day: Day)
}