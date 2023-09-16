package com.githukudenis.intimo.feature.habit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.feature.habit.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HorizontalDateView(
    selectedDate: LocalDate,
    dates: List<Date>,
    completedDates: List<LocalDate>,
    onPrevWeekListener: (LocalDate) -> Unit,
    onNextWeekListener: (LocalDate) -> Unit,
    onChangeDate: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onPrevWeekListener(selectedDate) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(id = R.string.prev_week),
                )
            }
            Text(
                text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                style = MaterialTheme.typography.labelMedium
            )
            IconButton(
                enabled = selectedDate < LocalDate.now(),
                onClick = { onNextWeekListener(selectedDate) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.next_week),
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = dates, key = { it.date }) { dateItem ->
                DatePill(
                    dateItem = dateItem,
                    selected = dateItem.date == selectedDate,
                    onChangeDate = onChangeDate,
                    completed = dateItem.date in completedDates
                )
            }
        }
    }
}