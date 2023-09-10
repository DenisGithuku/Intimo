package com.githukudenis.summary.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.feature.summary.R

@Composable
fun InfoChip(
    habitStatus: HabitStatus,
    shape: Shape = MaterialTheme.shapes.small,
) {

    val habitColor = when (habitStatus) {
        HabitStatus.UPCOMING -> {
            Color(0xFFF2A900)
        }

        HabitStatus.PENDING -> {
            MaterialTheme.colorScheme.error
        }

        HabitStatus.IN_PROGRESS -> {
            Color(0xFF1078CF)
        }

        HabitStatus.COMPLETE -> {
            MaterialTheme.colorScheme.primary
        }
        HabitStatus.DELAYED_START -> {
            Color(0xFFFFA663)
        }
    }
    Surface(
        shape = shape,
        border = BorderStroke(
            width = 1.dp,
            color = habitColor
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (habitStatus) {
                    HabitStatus.UPCOMING -> {
                        Icons.Outlined.Timelapse
                    }

                    HabitStatus.PENDING -> {
                        Icons.Outlined.TimerOff
                    }
                    HabitStatus.COMPLETE -> {
                        Icons.Outlined.Check
                    }

                    HabitStatus.DELAYED_START -> {
                        Icons.Outlined.AccessTime
                    }
                    HabitStatus.IN_PROGRESS -> {
                        Icons.Outlined.DirectionsRun
                    }
                },
                contentDescription = stringResource(R.string.habit_status_icon_desc),
                tint = habitColor,
                modifier = Modifier.size(12.dp)

            )
            Spacer(modifier = Modifier.width(4.dp))
            val habitName = if (habitStatus == HabitStatus.IN_PROGRESS || habitStatus == HabitStatus.DELAYED_START) {
                habitStatus.name.split("_").joinToString(" ").lowercase().replaceFirstChar { it.uppercase() }
            } else {
                habitStatus.name.lowercase().replaceFirstChar { it.uppercase() }
            }
            Text(
                text = habitName,
                style = MaterialTheme.typography.labelSmall,
                color = habitColor
            )
        }
    }
}

enum class HabitStatus {
    UPCOMING,
    DELAYED_START,
    IN_PROGRESS,
    PENDING,
    COMPLETE
}

@Preview
@Composable
fun AssistChipPrev() {
    InfoChip(habitStatus = HabitStatus.COMPLETE)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun InfoChipNightPrev() {
    InfoChip(habitStatus = HabitStatus.PENDING)
}
