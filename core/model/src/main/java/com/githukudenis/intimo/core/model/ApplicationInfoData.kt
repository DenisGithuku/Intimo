package com.githukudenis.intimo.core.model

import android.graphics.drawable.Drawable

data class ApplicationInfoData(
    val packageName: String,
    var icon: Drawable? = null,
    val colorSwatch: Int? = null,
    var usageDuration: Long = 0L,
    var usagePercentage: Float = 0F,
    var appLaunchCount: Int = 0
)
