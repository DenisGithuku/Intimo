package com.githukudenis.summary.util

import android.app.AppOpsManager
import android.content.Context
import android.os.Process

fun Context.hasUsageAccessPermissions(): Boolean {
    val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val permissionAllowed = appOpsManager.checkOpNoThrow(
        "android:get_usage_stats",
        Process.myUid(),
        packageName
    ) == AppOpsManager.MODE_ALLOWED
    return permissionAllowed
}