package com.githukudenis.intimo.feature.summary.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NotificationCard(
    habitPerformance: HabitPerformance,
    onTakeAction: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(
            width = 1.5.dp,
            color = when (habitPerformance) {
                HabitPerformance.EXCELLENT -> Color.Green.copy(alpha = 0.2f)
                HabitPerformance.GOOD -> Color.Blue.copy(alpha = 0.2f)
                HabitPerformance.POOR -> Color.LightGray.copy(alpha = 0.2f)
            }
        ),
        elevation = CardDefaults.outlinedCardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (habitPerformance) {
                HabitPerformance.EXCELLENT -> Color.Green.copy(alpha = 0.04f)
                HabitPerformance.GOOD -> Color.Blue.copy(alpha = 0.04f)
                HabitPerformance.POOR -> Color.Gray.copy(alpha = 0.04f)
            }
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = habitPerformance.comment,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = when (habitPerformance) {
                        HabitPerformance.EXCELLENT -> "\uD83D\uDD25"
                        HabitPerformance.GOOD -> "\uD83E\uDEE1"
                        HabitPerformance.POOR -> "\uD83D\uDE0A"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )

            }
            Spacer(modifier = Modifier.height(4.dp))
            FilledTonalButton(
                onClick = onTakeAction,
                contentPadding = PaddingValues(
                    vertical = 8.dp,
                    horizontal = 12.dp
                ), colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Take action",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

enum class HabitPerformance(val comment: String) {
    EXCELLENT(comment = "Your habit-building skills are like a well-tuned symphony, harmonious and impressive!"),
    GOOD(comment = "You're weaving the threads of a better routine, and the tapestry is starting to look fantastic."),
    POOR(comment = "Building habits can be like solving a puzzle. Let's figure out the missing piece and make progress together.")
}

@Preview
@Composable
fun NotificationCardPrev() {
    NotificationCard(habitPerformance = HabitPerformance.GOOD, onTakeAction = {})
}