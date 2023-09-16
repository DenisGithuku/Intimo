package com.githukudenis.intimo.core.model

data class DataUsageStats(
    val appUsageList: List<ApplicationInfoData> = emptyList(),
    val unlockCount: Int = 0,
    val notificationCount: Int = 0
)
