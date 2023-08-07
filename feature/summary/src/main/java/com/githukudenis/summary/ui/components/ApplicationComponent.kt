package com.githukudenis.summary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.githukudenis.model.ApplicationInfoData
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun ApplicationComponent(applicationInfoData: ApplicationInfoData) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberDrawablePainter(
                applicationInfoData.icon
            ), contentDescription = "App icon",
            modifier = Modifier.size(50.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = applicationInfoData.packageName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = applicationInfoData.usageDuration.toString(),
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = applicationInfoData.usagePercentage.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }
}