package com.githukudenis.summary.ui.components

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.nameToString
import com.githukudenis.summary.ui.HabitUiModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitCard(
    modifier: Modifier = Modifier,
    habitUiModel: HabitUiModel,
    onCheckHabit: (Long) -> Unit,
    onOpenHabitDetails: (Long) -> Unit
) {
    val context = LocalContext.current
    val checkmarkColor = animateColorAsState(
        targetValue = if (habitUiModel.completed) Color.Yellow.copy(green = 0.7f) else Color.LightGray,
        label = "Check mark color"
    )
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                MaterialTheme.colorScheme.surface,
            )
            .clickable {
                onOpenHabitDetails(habitUiModel.habitId)
            }
    )
    {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = habitUiModel.habitIcon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = habitUiModel.habitType.nameToString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = buildString {
                        append(getTimeString(
                            context = context,
                            timeInMillis = habitUiModel.startTime
                        ))
                        append(" - ")
                        append(getTimeString(
                            context = context,
                            timeInMillis = habitUiModel.startTime + habitUiModel.duration
                        ))
                    }
                )
            }
        }

        IconButton(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.TopEnd)
                .padding(4.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = Color.White,
            ),
            onClick = { onCheckHabit(habitUiModel.habitId) },
        ) {
            Icon(
                tint = checkmarkColor.value,
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.check_habit)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getTimeString(context: Context, timeInMillis: Long): String {
    val isIn24HourFormat = DateFormat.is24HourFormat(context)
    val formatter = if (isIn24HourFormat) {
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    } else {
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    }
    val parsedTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timeInMillis),
        ZoneId.systemDefault()
    )
    return parsedTime.format(formatter)
}