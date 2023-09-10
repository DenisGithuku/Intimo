package com.githukudenis.summary.ui.components

import android.content.Context
import android.content.res.Configuration
import android.text.format.DateFormat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.model.DurationType
import com.githukudenis.model.HabitType
import com.githukudenis.model.nameToString
import com.githukudenis.summary.ui.home.HabitUiModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitCard(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    habitUiModel: HabitUiModel,
    onOpenHabitDetails: (Long) -> Unit,
    onStart: (Long) -> Unit
) {
    val context = LocalContext.current

    val now = Calendar.getInstance()
    val habitTime = Calendar.getInstance().apply { timeInMillis = habitUiModel.startTime }
    val habitHour = habitTime.get(Calendar.HOUR_OF_DAY)
    val habitMinute = habitTime.get(Calendar.MINUTE)

    val startTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, habitHour)
        set(Calendar.MINUTE, habitMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }


    Box(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .clip(MaterialTheme.shapes.large)
            .border(
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (habitUiModel.completed) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    } else {
                        Color.Black.copy(
                            alpha = 0.1f
                        )
                    }
                )
            )
            .background(
                MaterialTheme.colorScheme.surface,
            )

            .clickable {
                onOpenHabitDetails(habitUiModel.habitId)
            }
    )
    {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habitUiModel.habitIcon,
                    style = MaterialTheme.typography.headlineLarge
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        habitStatus = if (habitUiModel.completed) {
                            HabitStatus.COMPLETE
                        } else if (isRunning) {
                            HabitStatus.IN_PROGRESS
                        } else if (startTime.timeInMillis < now.timeInMillis && now.timeInMillis < (startTime.timeInMillis + habitUiModel.duration)) {
                            HabitStatus.DELAYED_START
                        } else if (startTime.timeInMillis + habitUiModel.duration > now.timeInMillis) {
                            HabitStatus.UPCOMING
                        } else {
                            HabitStatus.PENDING
                        }
                    )
                    Text(
                        text = habitUiModel.habitType.nameToString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = buildString {
                            append(
                                getTimeString(
                                    context = context,
                                    timeInMillis = startTime.timeInMillis
                                )
                            )
                            append(" - ")
                            append(
                                getTimeString(
                                    context = context,
                                    timeInMillis = startTime.timeInMillis + habitUiModel.duration
                                )
                            )
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    )


                    Button(
                        enabled = !habitUiModel.completed,
                        onClick = { onStart(habitUiModel.habitId) }) {
                        Text(
                            text = if (isRunning) "Active" else "Start"
                        )
                    }
                }
            }
            if (isRunning && habitUiModel.remainingTime > 0L) {
                val animatedArcValue = animateFloatAsState(
                    targetValue = ((((habitUiModel.duration - habitUiModel.remainingTime)) * 100 / habitUiModel.duration) * 360f) / 100,
                    label = "arc value"
                )

                val arcValueColor = MaterialTheme.colorScheme.tertiary
                val arcBgColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.09f
                )

                Canvas(modifier = Modifier.size(60.dp)) {
                    drawArc(
                        color = arcBgColor,
                        startAngle = -90f,
                        useCenter = false,
                        sweepAngle = 360f,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = arcValueColor,
                        startAngle = -90f,
                        sweepAngle = animatedArcValue.value,
                        useCenter = false,
                        style = Stroke(
                            width = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}

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

@Preview()
@Composable
fun HabitCardPreview() {
    HabitCard(
        isRunning = true,
        habitUiModel = HabitUiModel(
            completed = false,
            habitIcon = "\uD83E\uDD38",
            habitType = HabitType.EXERCISE,
            startTime = 169023000000,
            duration = 1800000,
            durationType = DurationType.MINUTE
        ), onOpenHabitDetails = {}, onStart = {}
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun HabitCardNightPreview() {
    HabitCard(
        isRunning = false,
        habitUiModel = HabitUiModel(
            completed = false,
            habitIcon = "\uD83E\uDD38",
            habitType = HabitType.EXERCISE,
            startTime = 169023000000,
            duration = 1800000,
            durationType = DurationType.MINUTE
        ), onOpenHabitDetails = {}, onStart = {}
    )
}