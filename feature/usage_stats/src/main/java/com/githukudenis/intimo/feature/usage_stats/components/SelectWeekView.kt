package com.githukudenis.intimo.feature.usage_stats.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.MultipleClicksCutter
import com.githukudenis.intimo.core.ui.components.get
import com.githukudenis.intimo.feature.usage_stats.R
import java.time.LocalDate

@Composable
fun SelectedWeekView(
    selectedDate: LocalDate,
    multipleClicksCutter: MultipleClicksCutter = remember { MultipleClicksCutter.get() },
    onNextWeekListener: (LocalDate) -> Unit,
    onPrevWeekListener: (LocalDate) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    multipleClicksCutter.processEvent {
                        onPrevWeekListener(
                            selectedDate.minusDays(1)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                    contentDescription = stringResource(id = R.string.prev_week_icon_text),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }
            Text(
                text = buildString {
                    append(
                        selectedDate.dayOfWeek.name.take(3).lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                    append(" ")
                    append(selectedDate.dayOfMonth)
                    append(" ")
                    append(
                        selectedDate.month.name.take(3).lowercase()
                            .replaceFirstChar { it.uppercase() })
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            IconButton(
                enabled = selectedDate < LocalDate.now(),
                onClick = {
                    multipleClicksCutter.processEvent {
                        onNextWeekListener(
                            selectedDate.plusDays(
                                1
                            )
                        )
                    }
                }) {
                val tint = animateColorAsState(
                    targetValue = if (selectedDate < LocalDate.now()
                    ) {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    } else {
                        Color.Transparent
                    }
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.next_week_icon_text),
                    tint = tint.value
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectedWeekViewPrev() {
    SelectedWeekView(
        selectedDate = LocalDate.now(),
        onNextWeekListener = {},
        onPrevWeekListener = {},
    )
}