package com.githukudenis.intimo.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.clickableOnce


@Composable
fun ToggleableSettingsListView(
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    isToggledOn: Boolean = false,
    onToggle: (Boolean) -> Unit,
) {
    var isToggleOn by remember {
        mutableStateOf(isToggledOn)
    }
    Row(
        modifier = Modifier
            .clickableOnce {
                isToggleOn = !isToggleOn
                onToggle(isToggleOn)
            }
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            title()
            if (description != null) {
                description()
            }
        }
        Switch(checked = isToggleOn, onCheckedChange = {
            isToggleOn = it
            onToggle(isToggleOn)
        }
        )
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
            .clickableOnce(clickable) {
                onClick()
            }
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        title()
        if (description != null) {
            description()
        }
    }
}