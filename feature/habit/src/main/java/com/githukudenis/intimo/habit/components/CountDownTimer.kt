package com.githukudenis.intimo.habit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.habit.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CountDownTimer(
    modifier: Modifier = Modifier,
    totalTime: Long,
    currentTime: Long,
    isTimerRunning: Boolean,
    initialValue: Float = 1f,
    activeBarColor: Color = MaterialTheme.colorScheme.primary,
    inActiveBarColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    strokeWidth: Dp = 8.dp,
    onStartTimer: () -> Unit,
    onPauseTimer: (Long) -> Unit,
    onTimerFinished: () -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    var timerValue by remember { mutableFloatStateOf(initialValue) }


    LaunchedEffect(key1 = isTimerRunning, key2 = currentTime) {
        if (currentTime > 0 && isTimerRunning) {
//            delay(1000L)
//            onTimeChanged(currentTime - 1000L)
            timerValue = currentTime / totalTime.toFloat()
        }

        if (currentTime <= 0) {
            delay(1000L)
            onTimerFinished()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.onSizeChanged {
            size = it
        }
    ) {
        Canvas(modifier = modifier) {
            drawArc(
                color = inActiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * timerValue,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            val center = Offset(size.width / 2f, size.height / 2f)
            val sweepAngleDegrees = (250f * timerValue + 145f) * (PI / 180f).toFloat()
            val radius = size.width / 2f

            val sideA = cos(sweepAngleDegrees) * radius
            val sideB = sin(sweepAngleDegrees) * radius


            drawPoints(
                listOf(Offset(center.x + sideA, center.y + sideB)),
                pointMode = PointMode.Points,
                color = activeBarColor,
                strokeWidth = (strokeWidth * 3f).toPx(),
                cap = StrokeCap.Round
            )
        }
        Text(
            text = formatCountdownTime(currentTime),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )


        CountDownButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            icon = {
                val icon = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(id = R.string.habit_status_button_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
            }, onClick = {
                if (!isTimerRunning) {
                    onStartTimer()
                } else {
                    onPauseTimer(currentTime)
                }
            })
    }
}

@Composable
fun CountDownButton(
    modifier: Modifier = Modifier,
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
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        icon()
    }
}

fun formatCountdownTime(milliseconds: Long): String {
    val hours = (milliseconds / (1000 * 60 * 60)).toInt()
    val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val seconds = ((milliseconds % (1000 * 60)) / 1000).toInt()

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Preview
@Composable
fun CountDownTimerPrev() {
    CountDownTimer(
        modifier = Modifier.size(200.dp),
        totalTime = 1000L * 60,
        currentTime = 9000L * 60,
        isTimerRunning = false,
        onStartTimer = {},
        onTimerFinished = {},
        onPauseTimer = {}
    )
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