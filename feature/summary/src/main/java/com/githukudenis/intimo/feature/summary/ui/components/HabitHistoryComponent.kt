package com.githukudenis.intimo.feature.summary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.Date

@Composable
fun HabitHistoryComponent(
    habitProgress: Map<Date, Float>,
    selectedDate: Date = Date(isSelected = true, isToday = true),
    onSelectDay: (Date) -> Unit
) {
   LazyRow(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
       items(habitProgress.keys.toList(), key = {it.date}) { date ->
           Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
               Text(
                   text = date.date.dayOfWeek.name.first().uppercase(),
                   style = MaterialTheme.typography.labelSmall
               )
               Box(
                   modifier = Modifier
                       .requiredSizeIn(
                           30.dp
                       )
                       .clip(CircleShape)
                       .clickable { onSelectDay(date) },
                   contentAlignment = Alignment.Center
               ) {
                   Text(
                       text = date.date.dayOfMonth.toString(),
                       style = MaterialTheme.typography.labelSmall,
                       color = if (date == selectedDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                   )
                   CircularProgressIndicator(
                       progress = 1f,
                       strokeWidth = 2.dp,
                       color = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
                   )
                   CircularProgressIndicator(
                       progress = habitProgress.getValue(date),
                       strokeWidth = 2.dp,
                       color = MaterialTheme.colorScheme.primary
                   )
               }
           }
       }
   }
}