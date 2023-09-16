package com.githukudenis.intimo.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppInFocusMode(
    @PrimaryKey(autoGenerate = true)
    val appId: Long = 0,
    val packageName: String,
    val limitDuration: Long = 0L,
    val limitReached: Boolean = false
)
