package com.githukudenis.intimo.habit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.calendar.Date
import java.time.LocalDate

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

            Icon(
                imageVector = if (completed) Icons.Filled.Check else Icons.Outlined.Close,
                contentDescription = "Habit completed"
            )
        }
    }
}