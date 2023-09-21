package com.githukudenis.intimo.feature.habit.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.feature.habit.R

@Composable
fun CountDownTimer(
    modifier: Modifier = Modifier,
    totalTime: Long,
    habitName: String,
    currentTime: Long,
    isTimerRunning: Boolean,
    initialValue: Float = 1f,
    activeBarColor: Color = MaterialTheme.colorScheme.primary,
    inActiveBarColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    strokeWidth: Dp = 2.dp,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onRestartHabit: () -> Unit,
    onCancelHabit: () -> Unit,
    onTimerFinished: () -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    var timerValue by rememberSaveable { mutableFloatStateOf(initialValue) }

    var timerStarted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = isTimerRunning, key2 = currentTime) {
        if (currentTime > 0 && isTimerRunning) {
            timerValue = currentTime / totalTime.toFloat()
            timerStarted = true
        }

        if (currentTime <= 0) {
            onTimerFinished()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.onSizeChanged {
                size = it
            }
        ) {
            Canvas(modifier = modifier) {

                drawArc(
                    color = activeBarColor,
                    startAngle = -90f,
                    sweepAngle = 360f * timerValue,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Square)
                )
                drawArc(
                    color = inActiveBarColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
//                val center = Offset(size.width / 2f, size.height / 2f)
//                val sweepAngleDegrees = (250f * timerValue + 145f) * (PI / 180f).toFloat()
//                val radius = size.width / 2f

//                val sideA = cos(sweepAngleDegrees) * radius
//                val sideB = sin(sweepAngleDegrees) * radius


//                drawPoints(
//                    listOf(Offset(center.x + sideA, center.y + sideB)),
//                    pointMode = PointMode.Points,
//                    color = activeBarColor,
//                    strokeWidth = (strokeWidth * 3f).toPx(),
//                    cap = StrokeCap.Round
//                )

            }
            Text(
                text = habitName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatCountdownTime(currentTime),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp
                ),

                )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.habit_remaining_time_text),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountDownButton(
                enabled = isTimerRunning,
                icon = {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = stringResource(id = R.string.restart_habit),
                        tint = if (timerStarted) MaterialTheme.colorScheme.primary else Color.LightGray.copy(
                            0.5f
                        )
                    )
                },
                onClick = onRestartHabit
            )
            CountDownButton(
                icon = {
                    Crossfade(
                        targetState = isTimerRunning,
                        label = "pause_play_icon"
                    ) { isRunning ->
                        when (isRunning) {
                            true -> {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = stringResource(id = R.string.habit_status_button_icon),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            false -> {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(id = R.string.habit_status_button_icon),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                }, onClick = {
                    if (!timerStarted && !isTimerRunning) {
                        timerStarted = true
                        onStartTimer()
                    } else if (timerStarted && !isTimerRunning) {
                        onResumeTimer()
                    } else {
                        onPauseTimer()
                    }

                })
            CountDownButton(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Close, contentDescription = stringResource(
                            id = R.string.stop_habit
                        ), tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = onCancelHabit
            )
        }
    }
}

@Composable
fun CountDownButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .clickableOnce(enabled = enabled) { onClick() }
            .padding(12.dp)
    ) {
        icon()
    }
}

fun formatCountdownTime(milliseconds: Long): String {
    val hours = (milliseconds / (1000 * 60 * 60)).toInt()
    val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val seconds = ((milliseconds % (1000 * 60)) / 1000).toInt()

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Preview
@Composable
fun CountDownTimerPrev() {
    CountDownTimer(
        modifier = Modifier.size(200.dp),
        totalTime = 1000L * 60,
        habitName = "Sleeping",
        currentTime = 9000L * 60,
        isTimerRunning = false,
        onStartTimer = {},
        onPauseTimer = {},
        onResumeTimer = {},
        onRestartHabit = {},
        onCancelHabit = {}
    ) {}
}

@Preview
@Composable
fun CountDownButtonPrev() {
    CountDownButton(
        icon = {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = {}
    )
}