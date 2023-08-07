package com.githukudenis.summary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.githukudenis.model.HabitData
import com.githukudenis.model.nameToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habitData: HabitData,
    onOpenHabitDetails: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.06f))
            .clickable {
                onOpenHabitDetails(habitData.habitDataId)
            }
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = habitData.habitIcon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            Column {
                Text(
                    text = habitData.habitType.nameToString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = habitData.habitPoints.toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}