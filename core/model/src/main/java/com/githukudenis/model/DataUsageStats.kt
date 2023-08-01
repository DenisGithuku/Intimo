package com.githukudenis.model

data class DataUsageStats(
    val appUsageList: List<ApplicationInfoData> = emptyList(),
    val unlockCount: Int = 0
)