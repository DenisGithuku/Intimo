package com.githukudenis.intimo.habit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HabitDetailListItem(
    icon: @Composable (() -> Unit)? = null,
    label: @Composable () -> Unit,
    description: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                label()
                Spacer(modifier = Modifier.height(6.dp))
                if (description != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides LocalContentColor.current.copy(
                            alpha = 0.7f
                        )
                    ) {
                        description()
                    }
                }
            }
        }
            if (action != null) {
                action()
            }

    }
}

@Preview
@Composable
fun HabitDetailListItemPrev() {
    HabitDetailListItem(
        icon = {
            Icon(imageVector = Icons.Outlined.ModeNight, contentDescription = "Theme")
        },
        label = {
            Text(
                text = "Theme"
            )
        },
        description = {
            Text(
                text = "Dark",
                style = MaterialTheme.typography.labelSmall
            )
        },
        action = {
            Switch(checked = false, onCheckedChange = {})
        }
    )
}