package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.githukudenis.intimo.core.model.AppInFocusMode
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsInFocusModeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppInFocusMode(appInFocusMode: AppInFocusMode)

    @Query("SELECT * FROM AppInFocusMode")
    fun getAllAppsInFocusMode(): Flow<List<AppInFocusMode>>

    @Delete
    fun deleteAppFromFocusMode(appInFocusMode: AppInFocusMode)

    @Update
    fun updateAppInFocusMode(appInFocusMode: AppInFocusMode)
}