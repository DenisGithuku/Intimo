package com.githukudenis.intimo.habit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CountDownTimer(
    modifier: Modifier = Modifier,
    totalTime: Long,
    currentTime: Long,
    onTimeChanged: (Long) -> Unit,
    isTimerRunning: Boolean,
    initialValue: Float = 1f,
    activeBarColor: Color = MaterialTheme.colorScheme.primary,
    inActiveBarColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
    strokeWidth: Dp = 8.dp,
    onToggleTimer: () -> Unit,
    onTimerFinished: () -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    var timerValue by remember { mutableFloatStateOf(initialValue) }


    LaunchedEffect(key1 = isTimerRunning, key2 = currentTime) {
        if (currentTime > 0 && isTimerRunning) {
            delay(1000L)
            onTimeChanged(currentTime - 1000L)
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

        Button(
            onClick = {
                onToggleTimer()
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isTimerRunning || currentTime <= 0L) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                text = if (!isTimerRunning && currentTime >= 0L) {
                    "Start"
                } else {
                    "Stop"
                }
            )
        }
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
        onTimeChanged = {},
        onToggleTimer = {},
        onTimerFinished = {}
    )
}