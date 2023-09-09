package com.githukudenis.intimo.habit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.githukudenis.intimo.habit.detail.formatDurationMillis

@Composable
fun HabitDurationDialog(
    durationValue: Long,
    onDismissRequest: (Long) -> Unit
) {

    var selectedDuration by remember { mutableLongStateOf(durationValue) }

    val durationList = remember {
        listOf(
            3000L * 60,
            5000L * 60,
            10000L * 60,
            15000L * 60,
            20000L * 60,
            25000L * 60,
            30000L * 60,
            45000L * 60,
            1000L * 60 * 60,
            2000L * 60 * 60,
            3000L * 60 * 60,
        )
    }


    Dialog(
        onDismissRequest = {
            onDismissRequest(selectedDuration)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Habit duration",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Divider(
                    thickness = 0.7.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, false)
                ) {
                    items(durationList) { duration ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    selectedDuration = duration
                                    onDismissRequest(selectedDuration)
                                }
                                .padding(4.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = duration == selectedDuration,
                                onClick = {
                                    selectedDuration = duration
                                    onDismissRequest(selectedDuration)
                                })
                            Text(
                                text = formatDurationMillis(duration),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Divider(
                    thickness = 0.7.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onDismissRequest(
                            selectedDuration
                        )
                    }) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }
            }
        }
    }
}