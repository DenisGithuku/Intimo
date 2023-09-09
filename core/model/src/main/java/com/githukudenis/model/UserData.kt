package com.githukudenis.model

data class UserData(
    val shouldHideOnBoarding: Boolean,
    val notificationCount: Long = 0L,
    val remainingHabitTime: Long = 0L
)