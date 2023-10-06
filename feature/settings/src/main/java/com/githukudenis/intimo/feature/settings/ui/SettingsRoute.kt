package com.githukudenis.intimo.feature.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.githukudenis.intimo.core.model.Theme
import com.githukudenis.intimo.core.ui.components.IntimoAlertDialog
import com.githukudenis.intimo.core.ui.components.MultipleClicksCutter
import com.githukudenis.intimo.core.ui.components.clickableOnce
import com.githukudenis.intimo.core.ui.components.get
import com.githukudenis.intimo.feature.settings.R
import com.githukudenis.intimo.feature.settings.work.HabitRemindersWorker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onOpenLicenses: () -> Unit,
    onRequestInAppReview: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scrollBehaviour =
        TopAppBarDefaults.enterAlwaysScrollBehavior()

    val multipleClicksCutter = remember {
        MultipleClicksCutter.get()
    }

    var optionsMenuIsVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var optionsMenuOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }

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
                    IconButton(onClick = { multipleClicksCutter.processEvent(onNavigateUp) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up_icon)
                        )
                    }
                },
                scrollBehavior = scrollBehaviour,
                actions = {
                    IconButton(
                        onClick = {
                            optionsMenuIsVisible = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    DropdownMenu(
                        expanded = optionsMenuIsVisible,
                        onDismissRequest = { optionsMenuIsVisible = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Privacy policy",
                                )
                            }, onClick = {
                                optionsMenuIsVisible = false
                                val policyUrl =
                                    "https://sites.google.com/view/gitsoftapps-intimo/home"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(policyUrl)
                                }
                                context.startActivity(intent)
                            })
                        DropdownMenuItem(
                            text = { Text(text=  "Open source licenses") },
                            onClick = {
                                optionsMenuIsVisible = false
                                onOpenLicenses()
                            })
                    }

                }
            )
        }
    ) { paddingValues ->

        SettingsScreen(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            ),
            theme = uiState.theme,
            onChangeTheme = { viewModel.onToggleTheme(it) },
            isDeviceUsageNotificationsAllowed = uiState.deviceUsageNotificationsAllowed,
            isHabitRemindersAllowed = uiState.habitNotificationsAllowed,
            onToggleHabitAlerts = { isEnabled ->
                viewModel.setShouldAllowHabitsNotifications(isEnabled)

                setupHabitReminders(isEnabled, context)

            },
            onOpenLicenses = onOpenLicenses,
            onRequestInAppReview = onRequestInAppReview
        )
    }
}

fun setupHabitReminders(enabled: Boolean, context: Context) {
    val workManager = WorkManager.getInstance(context)
    if (enabled) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()

        val request = PeriodicWorkRequestBuilder<HabitRemindersWorker>(2, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("habit_reminders_work")
            .build()

        workManager
            .enqueueUniquePeriodicWork(
                "habit_reminders_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )

    } else {
        workManager.cancelAllWorkByTag("habit_reminders_work")
    }
}

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(16.dp),
    theme: Theme,
    onChangeTheme: (Theme) -> Unit,
    isDeviceUsageNotificationsAllowed: Boolean,
    isHabitRemindersAllowed: Boolean,
    onToggleHabitAlerts: (Boolean) -> Unit,
    onOpenLicenses: () -> Unit,
    onRequestInAppReview: () -> Unit,
) {
    val context = LocalContext.current

    val availableThemes = remember {
        listOf(
            Theme.SYSTEM,
            Theme.LIGHT,
            Theme.DARK
        )
    }

    var selectedTheme by remember(theme) { mutableStateOf(theme) }

    var themeDialogVisible by remember {
        mutableStateOf(false)
    }

    var usageNotificationsAllowed by remember {
        mutableStateOf(isDeviceUsageNotificationsAllowed)
    }

    var habitNotificationsAllowed by remember(isHabitRemindersAllowed) {
        mutableStateOf(isHabitRemindersAllowed)
    }

    if (themeDialogVisible) {
        IntimoAlertDialog(
            title = {
                Text(
                    text = "App theme",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            content = {
                Column {
                    availableThemes.forEach { availableTheme ->
                        Row(
                            modifier = Modifier
                                .clickableOnce {
                                    selectedTheme = availableTheme
                                    onChangeTheme(availableTheme)
                                }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = availableTheme == selectedTheme,
                                onClick = {
                                    selectedTheme = availableTheme
                                    onChangeTheme(availableTheme)
                                })
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
                        style = MaterialTheme.typography.titleSmall
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
            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )
        }
        item {
            SettingsSectionTitle(text = stringResource(R.string.notifications_section_title))
        }
        item {
            ToggleableSettingsListView(
                isToggledOn = habitNotificationsAllowed,
                title = {
                    Text(
                        text = stringResource(R.string.habit_reminders_title),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                description = {
                    Text(
                        text = stringResource(
                            R.string.periodic_updates_description,
                            if (!habitNotificationsAllowed) "Enable" else "Disable"
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onToggle = { isAllowed ->
                    habitNotificationsAllowed = isAllowed
                    onToggleHabitAlerts(isAllowed)
                }
            )
        }
        item {
            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )
        }
        item {
            SettingsSectionTitle(text = stringResource(R.string.other_settings_section_tittle))
        }
        item {
            SettingsListView(
                title = {
                    Text(
                        text = stringResource(id = R.string.rate_app_title),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                description = {
                    Text(
                        text = stringResource(id = R.string.rate_app_description),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                clickable = true,
                onClick = onRequestInAppReview
            )
        }
        item {
            SettingsListView(
                title = {
                    Text(
                        text = stringResource(R.string.app_version_title),
                        style = MaterialTheme.typography.titleSmall
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
    }
}