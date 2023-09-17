package com.githukudenis.intimo.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ToggleableSettingsListView(
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    isToggledOn: Boolean = false,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsListView(
            modifier = Modifier.weight(1f, false),
            title = title,
            description = description
        )
        Switch(checked = isToggledOn, onCheckedChange = onToggle)
    }
}


@Composable
fun SettingsListView(
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clickable(clickable) {
                onClick()
            }
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        title()
        if (description != null) {
            description()
        }
    }
}