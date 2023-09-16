package com.githukudenis.intimo.feature.util

import android.content.Context
import android.provider.Settings

fun Context.hasWindowOverlayPermission(): Boolean {
    return Settings.canDrawOverlays(this)
}