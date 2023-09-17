package com.githukudenis.intimo.core.model

data class UserData(
    val shouldHideOnBoarding: Boolean,
    val deviceUsageNotificationsAllowed: Boolean,
    val habitNotificationsAllowed: Boolean,
    val theme: Theme = Theme.SYSTEM
)