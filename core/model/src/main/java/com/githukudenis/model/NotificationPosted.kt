package com.githukudenis.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationPosted(
    @PrimaryKey(autoGenerate = true)
    val notifPrimaryId: Long = 0L,
    val notificationId: Int,
    val packageName: String
)
