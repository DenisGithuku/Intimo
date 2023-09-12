package com.githukudenis.intimo.feature.summary.util

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.core.app.NotificationManagerCompat

fun Context.hasUsageAccessPermissions(): Boolean {
    val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return appOpsManager.checkOpNoThrow(
        "android:get_usage_stats",
        Process.myUid(),
        packageName
    ) == AppOpsManager.MODE_ALLOWED
}

fun Context.hasNotificationAccessPermissions(): Boolean {
    val packageName: String = this.packageName
    val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(this)
    return enabledPackages.contains(packageName)
}