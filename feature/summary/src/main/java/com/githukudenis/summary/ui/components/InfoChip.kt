package com.githukudenis.summary.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
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
            Color.Yellow.copy(green = .7f)
        }

        HabitStatus.PENDING -> {
            MaterialTheme.colorScheme.error
        }

        HabitStatus.ONGOING -> {
            Color.Blue
        }

        HabitStatus.COMPLETE -> {
            MaterialTheme.colorScheme.primary
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

                    HabitStatus.ONGOING -> {
                        Icons.Outlined.AccessTime
                    }

                    HabitStatus.COMPLETE -> {
                        Icons.Outlined.Check
                    }
                }, contentDescription = stringResource(R.string.habit_status_icon_desc), tint = habitColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = habitStatus.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = habitColor
            )
        }
    }
}

enum class HabitStatus {
    UPCOMING,
    ONGOING,
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
