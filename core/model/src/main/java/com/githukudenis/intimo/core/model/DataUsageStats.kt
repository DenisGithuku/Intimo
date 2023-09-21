package com.githukudenis.intimo.core.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DataUsageStats(
    val appUsageList: ImmutableList<ApplicationInfoData> = persistentListOf(),
    val unlockCount: Int = 0,
    val notificationCount: Int = 0
)
