package com.githukudenis.intimo.feature.usage_stats.components

import android.graphics.Paint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.util.TimeFormatter
import com.githukudenis.intimo.feature.usage_stats.ChartState
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun UsageChart(
    chartState: ChartState,
    barCornersRadius: Float = 25f,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barWidth: Float = 25f,
    labelOffset: Float = 40f,
    chartHeight: Dp = 150.dp,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    topStartRadius: Dp = 0.dp,
    topEndRadius: Dp = 0.dp,
    bottomStartRadius: Dp = 0.dp,
    bottomEndRadius: Dp = 0.dp,
    onChangeDateListener: (LocalDate) -> Unit,
) {
    val colors = remember {
        listOf(
            Color.LightGray.copy(alpha = 0.4f),
            Color.LightGray.copy(alpha = 0.1f),
            Color.LightGray.copy(alpha = 0.4f),
        )
    }

    val infiniteTransition =
        rememberInfiniteTransition(label = "infinite transition loading skeleton")
    val transitionAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                delayMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "loading skeleton"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = transitionAnimation.value, y = transitionAnimation.value)
    )

    val shape = RoundedCornerShape(
        topStart = topStartRadius,
        topEnd = topEndRadius,
        bottomEnd = bottomEndRadius,
        bottomStart = bottomStartRadius
    )

    var screenSize by remember {
        mutableStateOf(Size.Zero)
    }

    var chosenBar by remember {
        mutableStateOf(-1)
    }
    var chosenBarKey by remember {
        mutableStateOf("")
    }

    var cHeight by remember {
        mutableStateOf(0.dp)
    }

    val animatedChartHeight by animateDpAsState(
        targetValue = cHeight,
        animationSpec = tween(
            1000,
            easing = LinearOutSlowInEasing
        ),
        label = "card height"
    )


    LaunchedEffect(chosenBar) {
        delay(3000)
        chosenBarKey = ""
    }

    when (chartState) {
        ChartState.Loading -> {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .matchParentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(brush))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.6f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.5f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.2f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.4f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.2f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.8f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight(0.45f)
                                    .width(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush = brush)
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(0.6f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(brush)
                    )
                }
            }
        }

        is ChartState.Loaded -> {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .border(width =1.dp, color = MaterialTheme.colorScheme.background.copy(
                        alpha = 0.1f
                    ), shape = MaterialTheme.shapes.medium)
                    .animateContentSize()
            ) {
                
                LaunchedEffect(key1 = chartHeight) {
                    cHeight = chartHeight
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = TimeFormatter.getTimeFromMillis(
                            chartState.data[chartState.selectedDate]?.second ?: 0L
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                    )

                    Canvas(modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(chartHeight)
                        .pointerInput(Unit) {
                            this.detectTapGestures(
                                onPress = {
                                chosenBar = detectPosition(
                                    screenSize = screenSize,
                                    offset = it,
                                    listSize = chartState.data.size,
                                    itemWidth = barWidth
                                )
                                if (chosenBar >= 0 && chartState.data.toList()[chosenBar].first <= LocalDate.now()) {
                                    onChangeDateListener(chartState.data.toList()[chosenBar].first)
                                    chosenBarKey =
                                        chartState.data.toList()[chosenBar].first.toString()
                                }
                            })
                        },
                        onDraw = {
                            screenSize = size
                            val spaceBetweenBars =
                                (size.width - (chartState.data.size * barWidth)) / (chartState.data.size - 1)
                            val maxBarHeight = chartState.data.values.maxOf { it.first }
                            val barScale = (size.height - 16.dp.toPx()) / maxBarHeight
                            val paint = Paint().apply {
                                this.color = labelColor.toArgb()
                                textAlign = Paint.Align.CENTER
                                textSize = 32f
                            }

                            var spaceStep = 0f

                            for (item in chartState.data) {
                                val topLeft = Offset(
                                    x = spaceStep,
                                    y = size.height - item.value.first * barScale - labelOffset
                                )
                                //--------------------(draw bars)--------------------//
                                drawRoundRect(
                                    color = if (item.key == chartState.selectedDate) barColor else barColor.copy(
                                        alpha = 0.1f
                                    ),
                                    topLeft = topLeft,
                                    size = Size(
                                        width = barWidth,
                                        height = size.height - topLeft.y - labelOffset
                                    ),
                                    cornerRadius = CornerRadius(barCornersRadius, barCornersRadius)
                                )
                                //--------------------(showing the x axis labels)--------------------//
                                drawContext.canvas.nativeCanvas.drawText(
                                    item.key.dayOfWeek.name.take(3).lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    spaceStep + barWidth / 2,
                                    size.height,
                                    paint
                                )
                                //--------------------(showing the bar label)--------------------//
                                if (chosenBarKey == item.key.toString()) {
                                    drawRoundRect(
                                        color = barColor.copy(alpha = 0.1f),
                                        topLeft = Offset(x = topLeft.x - 40f, y = topLeft.y - 100),
                                        size = Size(140f, 80f),
                                        cornerRadius = CornerRadius(40f, 40f)
                                    )
                                    val path = Path().apply {
                                        moveTo(topLeft.x + 50f, topLeft.y - 20)
                                        lineTo(topLeft.x + 25f, topLeft.y)
                                        lineTo(topLeft.x, topLeft.y - 20)
                                        lineTo(topLeft.x + 50f, topLeft.y - 20)
                                    }
                                    drawIntoCanvas { canvas ->
                                        canvas.drawOutline(
                                            outline = Outline.Generic(path = path),
                                            paint = androidx.compose.ui.graphics.Paint().apply {
                                                color = barColor.copy(alpha = 0.1f)
                                            })
                                    }

                                    drawContext.canvas.nativeCanvas.drawText(
                                        TimeFormatter.getTimeFromMillis(item.value.second),
                                        topLeft.x + 25,
                                        topLeft.y - 50,
                                        Paint().apply {
                                            textAlign = Paint.Align.CENTER
                                            textSize = 24f
                                            color = barColor.copy(alpha = 0.8f).toArgb()
                                        }
                                    )
                                }

                                spaceStep += spaceBetweenBars + barWidth
                            }
                        })
                    SelectedWeekView(
                        selectedDate = chartState.selectedDate,
                        onChangeDateListener = onChangeDateListener,
                    )
                }
            }
        }
    }
}


private fun detectPosition(screenSize: Size, offset: Offset, listSize: Int, itemWidth: Float): Int {
    val spaceBetweenBars =
        (screenSize.width - (listSize * itemWidth)) / (listSize - 1)
    var spaceStep = 0f
    for (i in 0 until listSize) {
        if (offset.x in spaceStep..(spaceStep + itemWidth)) {
            return i
        }
        spaceStep += spaceBetweenBars + itemWidth
    }
    return -1
}