package com.githukudenis.intimo.feature.summary.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.githukudenis.intimo.core.model.HabitFrequency
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSummaryScreenUiState(
    startTime: Long,
    pickerState: TimePickerState
): SummaryScreenUiState = remember {
    SummaryScreenUiState(
        pickerState = pickerState
    )
}

@ExperimentalMaterial3Api data class SummaryScreenUiState(
    val bottomSheetIsVisble: Boolean = false,
    val habitFrequency: List<HabitFrequency> = listOf(HabitFrequency.DAILY, HabitFrequency.WEEKLY),
    val initialTime: Calendar = Calendar.getInstance(),
    val habitReminderTimeDialogIsVisible: Boolean = false,
    var showPicker: Boolean = false,
    var habitDurationDialogVisible: Boolean = false,
    val pickerState: TimePickerState,
) {
    val timeFormatter = DateTimeFormatter.ofPattern(
        if (pickerState.is24hour) "hh:mm" else "hh:mm a", Locale.getDefault()
    )
}
//)val pickerState = rememberTimePickerState(
//    initialHour = if (initialTime.value.get(Calendar.HOUR_OF_DAY) <= 0L) {
//        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//    } else {
//        initialTime.value.get(Calendar.HOUR_OF_DAY)
//    }, initialMinute = if (initialTime.value.get(Calendar.MINUTE) <= 0L) {
//        Calendar.getInstance().get(Calendar.MINUTE)
//    } else {
//        initialTime.value.get(Calendar.MINUTE)
//    }
//
//)