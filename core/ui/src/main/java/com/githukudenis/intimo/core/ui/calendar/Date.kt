package com.githukudenis.intimo.core.ui.calendar

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(VERSION_CODES.O)
@Stable
data class Date(
    val date: LocalDate = LocalDate.now(),
    val isSelected: Boolean = false,
    val isToday: Boolean = false
)

@Stable
data class DateUiModel(
    val selectedDate: Date? = null,
    val availableDates: List<Date> = emptyList()
)

@RequiresApi(VERSION_CODES.O)
@Stable
class DateUiState(
    initialSelectedDate: LocalDate
) {

    private var today: LocalDate = LocalDate.now()

    var currentSelectedDate by mutableStateOf(initialSelectedDate)

    var dateUiModel by mutableStateOf(DateUiModel())

    init {
        setData()
    }


    fun setData(startDate: LocalDate = today) {
        val lastSevenDays = startDate.minusDays(4)
        dateUiModel = dateUiModel.copy(
            selectedDate = Date(
                date = currentSelectedDate,
                isSelected = true,
                isToday = currentSelectedDate == today
            ),
            availableDates = getDates(lastSevenDays, startDate)
        )
    }

    private fun getDates(startDate: LocalDate, endDate: LocalDate): List<Date> {
        val numOfDays = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1))

        return generateSequence(startDate) { date ->
            date.plusDays(1)
        }.take(numOfDays.toInt())
            .map {
                Date(
                    date = it,
                    isSelected = it == currentSelectedDate,
                    isToday = it == today
                )
            }
            .toList()
    }

    fun updateDate(newDate: LocalDate) {
        currentSelectedDate = newDate
    }
}

@RequiresApi(VERSION_CODES.O)
@Composable
fun rememberDateUiState(
    selectedDate: LocalDate
): DateUiState {
    return remember(selectedDate) {
        DateUiState(selectedDate)
    }
}

