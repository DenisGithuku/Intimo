package com.githukudenis.summary.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.ui.calendar.Date
import com.githukudenis.intimo.core.ui.calendar.rememberDateUiState
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.HabitType
import com.githukudenis.summary.ui.home.HabitUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HabitDetailRoute(
    habitDetailViewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by habitDetailViewModel.uiState.collectAsStateWithLifecycle()

    HabitDetailScreen(
        uiState = uiState,
        onCompleted = {
            habitDetailViewModel.onHabitComplete(it)
        },
        onChangeDate = {
            habitDetailViewModel.onChangeDate(it)
        }
    )
}

@Composable
private fun HabitDetailScreen(
    uiState: HabitDetailUiState,
    onCompleted: (Long) -> Unit,
    onChangeDate: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val dateUiState = rememberDateUiState(LocalDate.now())

        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))


        HorizontalDateView(
            selectedDate = dateUiState.currentSelectedDate,
            dates = dateUiState.dateUiModel.availableDates,
            onNextWeekListener = { localDate ->
                if (dateUiState.currentSelectedDate < LocalDate.now()) {
                    dateUiState.setData(
                        localDate.plusDays(1)
                    )
                    dateUiState.updateDate(localDate.plusDays(1))
                }
            },
            onPrevWeekListener = { localDate ->
                dateUiState.setData(localDate.minusDays(2))
                dateUiState.updateDate(localDate.minusDays(2))
            },
            onChangeDate = {
                dateUiState.updateDate(it)
            },
            completedDates = uiState.completedDayList
        )
    }
}


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

@Composable
fun HabitContents(
    habitDetail: HabitUiModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {

    }
}

@Composable
fun DatePill(
    dateItem: Date,
    selected: Boolean,
    completed: Boolean = false,
    onChangeDate: (LocalDate) -> Unit
) {

    Box(
        modifier = Modifier
            .size(60.dp, 80.dp)
            .clip(MaterialTheme.shapes.large)
            .border(
                shape = MaterialTheme.shapes.large,
                border = if (dateItem.isToday) BorderStroke(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.07f)
                ) else BorderStroke(width = 0.dp, color = Color.Transparent)
            )
            .background(
                color = if (selected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.background
            )
            .clickable { onChangeDate(dateItem.date) }, contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateItem.date.dayOfWeek.name.take(3).lowercase()
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${dateItem.date.dayOfMonth}",
                style = MaterialTheme.typography.labelMedium,
            )
            if (completed) {
                Icon(
                    imageVector = Icons.Filled.CheckCircleOutline,
                    contentDescription = "Habit completed"
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectedPillPrev() {
    DatePill(dateItem = Date(isToday = true), selected = true, onChangeDate = {})
}

@Preview
@Composable
fun UnselectedPillPrev() {
    DatePill(dateItem = Date(isToday = true), selected = false, onChangeDate = {})
}

@Preview(name = "Habit contents preview")
@Composable
fun HabitContentsPrev() {
    HabitContents(habitDetail = HabitUiModel(habitIcon = "", habitType = HabitType.EXERCISE))
}