package com.githukudenis.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["dayId", "notifPrimaryId"], indices = [Index("notifPrimaryId")])
data class DayAndNotificationsPostedCrossRef(
    val dayId: Long,
    val notifPrimaryId: Long
)

data class DayAndNotifications(
    @Embedded
    val day: Day,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "notifPrimaryId",
        associateBy = Junction(
            DayAndNotificationsPostedCrossRef::class
        )
    )
    val notifications: List<NotificationPosted>
)



