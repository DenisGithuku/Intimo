package com.githukudenis.model

import android.graphics.drawable.Drawable

data class ApplicationInfoData(
    val packageName: String,
    var icon: Drawable? = null,
    val usageDuration: Long = 0L,
    var usagePercentage: Float = 0F,
    val appLaunchCount: Int = 0
)