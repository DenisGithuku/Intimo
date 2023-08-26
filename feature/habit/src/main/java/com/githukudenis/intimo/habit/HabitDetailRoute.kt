package com.githukudenis.intimo.habit


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.ui.calendar.Date
import com.githukudenis.intimo.core.ui.calendar.rememberDateUiState
import com.githukudenis.intimo.habit.components.DatePill
import com.githukudenis.model.HabitType
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
    )
}

@Composable
private fun HabitDetailScreen(
    uiState: HabitDetailUiState,
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