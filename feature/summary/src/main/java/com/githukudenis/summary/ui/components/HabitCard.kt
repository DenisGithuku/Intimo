package com.githukudenis.summary.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.HabitData
import com.githukudenis.model.nameToString

@Composable
fun HabitCard(
    modifier: Modifier = Modifier,
    habitData: HabitData,
    checked: Boolean,
    onCheckHabit: (Int) -> Unit,
    onOpenHabitDetails: (Int) -> Unit
) {
    val checkmarkColor = animateColorAsState(
        targetValue = if (checked) Color.Yellow.copy(green = 0.7f) else Color.LightGray,
        label = "Check mark color"
    )
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.06f))
            .clickable {
                onOpenHabitDetails(habitData.habitDataId)
            }
    )
    {
        Column(
            modifier = Modifier
                .padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = habitData.habitIcon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = habitData.habitType.nameToString(),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = habitData.habitPoints.toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.TopEnd)
                .padding(4.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = Color.White,
            ),
            onClick = { onCheckHabit(habitData.habitDataId) },
        ) {
            Icon(
                tint = checkmarkColor.value,
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.check_habit)
            )
        }
    }
}