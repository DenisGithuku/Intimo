package com.githukudenis.intimo.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.Theme
import com.githukudenis.intimo.core.ui.components.IntimoAlertDialog
import com.githukudenis.intimo.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onOpenLicenses: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scrollBehaviour =
        TopAppBarDefaults.enterAlwaysScrollBehavior()

    LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings"
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up_icon)
                        )
                    }
                }, scrollBehavior = scrollBehaviour
            )
        }
    ) { paddingValues ->

        SettingsScreen(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp
            ),
            theme = uiState.theme,
            onChangeTheme = { viewModel.onToggleTheme(it) },
            isDeviceUsageNotificationsAllowed = uiState.deviceUsageNotificationsAllowed,
            onToggleDeviceUsageNotifications = { viewModel.setShouldAllowDeviceUsageNotifications(it) },
            isHabitRemindersAllowed = uiState.habitNotificationsAllowed,
            onToggleHabitAlerts = { viewModel.setShouldAllowHabitsNotifications(it) },
            onOpenLicenses = onOpenLicenses
        )
    }
}

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(16.dp),
    theme: Theme,
    onChangeTheme: (Theme) -> Unit,
    isDeviceUsageNotificationsAllowed: Boolean,
    onToggleDeviceUsageNotifications: (Boolean) -> Unit,
    isHabitRemindersAllowed: Boolean,
    onToggleHabitAlerts: (Boolean) -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val context = LocalContext.current

    val availableThemes = remember {
        listOf(
            Theme.SYSTEM,
            Theme.LIGHT,
            Theme.DARK
        )
    }

    var themeDialogVisible by remember {
        mutableStateOf(false)
    }

    if (themeDialogVisible) {
        IntimoAlertDialog(
            title = {
                Text(
                    text = "App theme",
                    style = MaterialTheme.typography.displaySmall
                )
            },
            content = {
                Column {
                    availableThemes.forEach { availableTheme ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = availableTheme == theme,
                                onClick = { onChangeTheme(availableTheme) })
                            Text(
                                text = availableTheme.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            onDismissRequest = {
                themeDialogVisible = false
            }
        )
    }

    LazyColumn(
        contentPadding = contentPadding,
    ) {
        item {
            SettingsSectionTitle(text = stringResource(R.string.settings_general_section_title))
        }
        item {
            SettingsListView(
                clickable = true,
                onClick = {
                    themeDialogVisible = true
                },
                title = {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                description = {
                    Text(
                        text = theme.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
            )
        }
        item {
            SettingsSectionTitle(text = stringResource(R.string.notifications_section_title))
        }
        item {
            ToggleableSettingsListView(
                isToggledOn = isDeviceUsageNotificationsAllowed,
                title = {
                    Text(
                        text = stringResource(R.string.device_usage_title),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                description = {
                    Text(
                        text = stringResource(R.string.device_usage_description),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ),
                        style = MaterialTheme.typography.labelMedium

                    )
                },
                onToggle = onToggleDeviceUsageNotifications
            )
        }
        item {
            ToggleableSettingsListView(
                isToggledOn = isHabitRemindersAllowed,
                title = {
                    Text(
                        text = stringResource(R.string.habit_reminders_title),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                description = {
                    Text(
                        text = stringResource(R.string.periodic_updates_description),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )

                },
                onToggle = onToggleHabitAlerts
            )
        }
        item {
            SettingsSectionTitle(text = stringResource(R.string.other_settings_section_tittle))
        }
        item {
            SettingsListView(
                title = {
                    Text(
                        text = stringResource(R.string.app_version_title),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                description = {
                    Text(
                        text = stringResource(
                            id = R.string.app_version_description,
                            context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).versionName
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        )
                    )
                }
            )
        }
        item {
            SettingsListView(
                clickable = true,
                onClick = onOpenLicenses,
                title = {
                    Text(
                        text = "Licenses"
                    )
                }
            )
        }
    }
}