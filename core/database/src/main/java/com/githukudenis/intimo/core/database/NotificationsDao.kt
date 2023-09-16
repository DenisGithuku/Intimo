package com.githukudenis.intimo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.githukudenis.intimo.core.model.NotificationPosted
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notificationPosted: NotificationPosted): Long

    @Query("SELECT * FROM NotificationPosted")
    fun getAllNotifications(): Flow<List<NotificationPosted>>

    @Query("SELECT * FROM NotificationPosted WHERE packageName LIKE :pkgName")
    fun getNotificationsByPackage(pkgName: String): Flow<List<NotificationPosted>>
}