package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.githukudenis.model.DayAndNotifications
import com.githukudenis.model.DayAndNotificationsPostedCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface DayAndNotificationsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDayAndNotification(dayAndNotificationsPostedCrossRef: DayAndNotificationsPostedCrossRef)

    @Transaction
    @Query("SELECT * FROM Day")
    fun getDayAndNotifications(): Flow<List<DayAndNotifications>>
}