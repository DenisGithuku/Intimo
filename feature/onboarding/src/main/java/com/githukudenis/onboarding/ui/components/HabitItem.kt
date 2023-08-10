package com.githukudenis.onboarding.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HabitItem(
    emoji: String,
    description: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        label = "Habit Background"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        label = "Habit border"
    )
    Box(
        modifier = Modifier
            .border(
                border = BorderStroke(width = 1.dp, color = borderColor),
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor)
            .clickable(onClick = {
                onToggle()
            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Habit Item Selected Prev")
@Composable
fun HabitItemPrev() {
    HabitItem(emoji = "\uD83D\uDCDA", description = "Reading", selected = true, onToggle = {})
}

@Preview(name = "Habit Item Unselected Prev")
@Composable
fun HabitItemUnSelectedPrev() {
    HabitItem(emoji = "\uD83D\uDCDA", description = "Reading", selected = false, onToggle = {})
}