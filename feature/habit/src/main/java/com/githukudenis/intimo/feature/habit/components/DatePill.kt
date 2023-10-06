package com.githukudenis.intimo.feature.habit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.ui.components.clickableOnce
import java.time.LocalDate

@Composable
fun DatePill(
    dateItem: Date, selected: Boolean, completed: Boolean = false, onChangeDate: (LocalDate) -> Unit
) {

    val animatedBackground =
        animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)


    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dateItem.date.dayOfWeek.name.take(3).lowercase()
                .replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                border = BorderStroke(
                    width = if (dateItem.isToday) 1.dp else 0.dp,
                    color = if (dateItem.isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background
                ), shape = CircleShape
            )
            .background(
                color = animatedBackground.value, shape = CircleShape
            )
            .clickableOnce { onChangeDate(dateItem.date) }, contentAlignment = Alignment.Center) {
            Text(
                text = "${dateItem.date.dayOfMonth}",
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        }

        Icon(
            imageVector = if (completed) Icons.Filled.Check else Icons.Outlined.Close,
            contentDescription = "Habit completed"
        )
    }
}