package com.githukudenis.intimo.usage_stats

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.util.TimeFormatter
import com.githukudenis.model.ApplicationInfoData
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun UsageStatsCard(
    applicationInfoData: ApplicationInfoData,
    onOpenLimitDialog: (systemApp: Boolean) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .clickable { }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            applicationInfoData.icon?.let {
                Image(
                    painter = rememberDrawablePainter(drawable = applicationInfoData.icon),
                    contentDescription = getApplicationLabel(
                        applicationInfoData.packageName,
                        context
                    ),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = getApplicationLabel(applicationInfoData.packageName, context),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = TimeFormatter.getTimeFromMillis(applicationInfoData.usageDuration),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .width(1.dp)
            )
            IconButton(onClick = {
                onOpenLimitDialog(isSystemApp(applicationInfoData.packageName, context))
            }) {
                Icon(
                    imageVector = if (isSystemApp(applicationInfoData.packageName, context)) {
                        Icons.Outlined.Info
                    } else {
                        Icons.Default.HourglassEmpty
                    },
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(id = R.string.limit_icon_text)
                )
            }
        }
    }
}

private fun isSystemApp(packageName: String, context: Context): Boolean {
    return try {
        val appFlags = context.packageManager.getApplicationInfo(packageName, 0)
        return (appFlags.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}