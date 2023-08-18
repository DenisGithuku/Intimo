package com.githukudenis.summary.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.summary.ui.components.CardInfo
import com.githukudenis.summary.ui.components.HabitCard
import com.githukudenis.summary.util.hasNotificationAccessPermissions
import com.githukudenis.summary.util.hasUsageAccessPermissions


@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun SummaryRoute(
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    onOpenHabitDetails: (Long) -> Unit
) {

    val context = LocalContext.current

    var shouldShowUsagePermissionsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var shouldShowNotificationPermissionsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val usageAccessPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (context.hasUsageAccessPermissions()) {
                summaryViewModel.onEvent(SummaryUiEvent.Refresh)
            } else {
                val userError = UserError(
                    message = "Usage access permissions required",
                    errorType = ErrorType.CRITICAL
                )
                summaryViewModel.onEvent(SummaryUiEvent.ShowError(userError))
            }
        }
    )

    val notificationAccessPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (context.hasNotificationAccessPermissions()) {
                summaryViewModel.onEvent(SummaryUiEvent.Refresh)
            } else {
                val userError = UserError(
                    message = "Notification access permissions required",
                    errorType = ErrorType.CRITICAL
                )
                summaryViewModel.onEvent(SummaryUiEvent.ShowError(userError))
            }
        }
    )

    val usagePermissionsAllowed = context.hasUsageAccessPermissions()
    val notificationAccessPermissionsAllowed = context.hasNotificationAccessPermissions()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, usagePermissionsAllowed, notificationAccessPermissionsAllowed) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    shouldShowUsagePermissionsDialog = !context.hasUsageAccessPermissions()
                    shouldShowNotificationPermissionsDialog =
                        !context.hasNotificationAccessPermissions()
                }

                else -> Unit
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    if (shouldShowUsagePermissionsDialog) {
        AlertDialog(
            title = {
                Text(
                    text = context.getString(R.string.usage_permission_dialog_title)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        usageAccessPermissionLauncher.launch(intent)
                    }
                ) {
                    Text(
                        text = context.getString(R.string.permission_dialog_positive_button)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        shouldShowUsagePermissionsDialog = false
                    }
                ) {
                    Text(
                        text = context.getString(R.string.permission_dialog_negative_button)
                    )
                }
            },
            text = {
                Text(
                    text = context.getString(R.string.usage_permission_dialog_description)
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = LocalTonalElevation.current.large,
            onDismissRequest = {
                shouldShowUsagePermissionsDialog = false
            }
        )
    }
    if (shouldShowNotificationPermissionsDialog) {
        AlertDialog(
            title = {
                Text(
                    text = context.getString(R.string.notification_access_permission_dialog_title)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent =
                            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                        usageAccessPermissionLauncher.launch(intent)
                    }
                ) {
                    Text(
                        text = context.getString(R.string.permission_dialog_positive_button)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        shouldShowUsagePermissionsDialog = false
                    }
                ) {
                    Text(
                        text = context.getString(R.string.permission_dialog_negative_button)
                    )
                }
            },
            text = {
                Text(
                    text = context.getString(R.string.notification_access_permission_dialog_description)
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = LocalTonalElevation.current.large,
            onDismissRequest = {
                shouldShowNotificationPermissionsDialog = false
            }
        )
    }

    val uiState by summaryViewModel.uiState.collectAsStateWithLifecycle()

    SummaryScreen(
        usageStats = uiState.summaryData?.usageStats?.appUsageList ?: emptyList(),
        unlockCount = uiState.summaryData?.unlockCount ?: 0,
        notificationCount = uiState.notificationCount,
        habitDataList = uiState.habitDataList,
        onCheckHabit = { habitId ->
            summaryViewModel.onEvent(SummaryUiEvent.CheckHabit(habitId))
        },
        onOpenHabit = { habitId -> onOpenHabitDetails(habitId) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun SummaryScreen(
    usageStats: List<ApplicationInfoData>,
    habitDataList: List<HabitUiModel>,
    unlockCount: Int,
    notificationCount: Long,
    onCheckHabit: (Long) -> Unit,
    onOpenHabit: (Long) -> Unit
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.screen_time),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        appUsageData(
            usageStats = usageStats,
            unlockCount = unlockCount,
            notificationCount = notificationCount,
            context = context
        )
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    text = "Your habits",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        habitList(
            habitDataList = habitDataList,
            onOpenHabit = onOpenHabit,
            onCheckHabit = onCheckHabit,
        )
    }
}

fun LazyListScope.appUsageData(
    usageStats: List<ApplicationInfoData>,
    unlockCount: Int,
    notificationCount: Long,
    context: Context
) {
    item {
        Surface(
            onClick = {
                Toast.makeText(context, "Open usage", Toast.LENGTH_SHORT).show()
            },
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(
                width = 1.dp,
                color = Color.Black.copy(
                    alpha = 0.1f
                )
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    /*
                        Get total app usage
                         */
                    val totalAppUsage = usageStats.sumOf { it.usageDuration.toInt() }.toFloat()

                    /*
                        splice most four used apps
                         */
                    val fourMostUsedApps =
                        usageStats.take(4).map { it.usageDuration.toFloat() }.toMutableList()

                    /*
                        get sum of remaining values
                         */
                    val remainingTotalUsage =
                        usageStats.drop(4).sumOf { it.usageDuration.toInt() }.toFloat()

                    /*
                        add remaining usage to first four apps
                         */
                    fourMostUsedApps.add(remainingTotalUsage)

                    /*
                        Create colors to map to usage duration
                         */
                    val colors = listOf(
                        Color.Blue.copy(green = .7f),
                        Color.Green.copy(),
                        Color.LightGray,
                        Color.Black.copy(alpha = .7f),
                        Color.Yellow.copy(green = .7f)
                    )

                    /*
                        create a combination of usage duration and color
                         */
                    val usageWithColors = fourMostUsedApps zip colors

                    /*
                        values to be plotted on canvas
                         */
                    val plotValues = usageWithColors.map { usageStats ->
                        usageStats.first * 100 / totalAppUsage
                    }

                    val animateArchValue = remember {
                        Animatable(0f)
                    }

                    LaunchedEffect(key1 = plotValues) {
                        animateArchValue.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 4000,
                                easing = EaseOut
                            )
                        )
                    }

                    /*
                        derive plot angles
                         */
                    val angles = plotValues.map {
                        it * 360f / 100
                    }

                    val textMeasurer = rememberTextMeasurer()
                    val totalAppTime = usageStats.sumOf { it.usageDuration }
                    val totalAppTimeText = getTimeFromMillis(totalAppTime)


                    val textLayoutResult = remember(totalAppTimeText) {
                        textMeasurer.measure(totalAppTimeText)
                    }
                    Spacer(
                        modifier = Modifier
                            .size(120.dp)
                            .drawWithCache {
                                var startAngle = -90f

                                onDrawBehind {
                                    for (i in angles.indices) {
                                        drawArc(
                                            color = usageWithColors.map { it.second }[i],
                                            startAngle = startAngle * animateArchValue.value,
                                            sweepAngle = angles[i],
                                            useCenter = false,
                                            style = Stroke(width = 16.dp.value)
                                        )
                                        startAngle += angles[i]
                                    }
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = totalAppTimeText,
                                        topLeft = Offset(
                                            x = center.x - textLayoutResult.size.width / 2,
                                            y = center.y - textLayoutResult.size.height / 2
                                        )
                                    )
                                }
                            })
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        usageWithColors.forEach { appUsage ->
                            val appName = usageStats
                                .find { it.usageDuration.toFloat() == appUsage.first }?.packageName?.let {
                                    getApplicationLabel(
                                        it,
                                        context
                                    )
                                } ?: "Other"
                            /*
                            Generate total use time for each
                            Use the value of other summed apps
                             */
                            val upTime =
                                usageStats.find { it.usageDuration.toFloat() == appUsage.first }?.usageDuration
                                    ?: fourMostUsedApps.last().toLong()
                            val formattedTime = getTimeFromMillis(upTime)
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(appUsage.second)
                                )
                                Text(
                                    text = buildString {
                                        append("$appName ")
                                        append(formattedTime)
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color = Color.Black.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CardInfo(
                        title = "Unlocks",
                        value = "$unlockCount"
                    )
                    CardInfo(
                        title = "Notifications",
                        value = "$notificationCount"
                    )
                }
            }
        }
    }
}


fun getTimeFromMillis(timeInMillis: Long): String {
    return if (timeInMillis / 1000 / 60 / 60 >= 1) {
        "${timeInMillis / 1000 / 60 / 60}hr ${timeInMillis / 1000 / 60 % 60}min"
    } else if (timeInMillis / 1000 / 60 >= 1) {
        "${timeInMillis / 1000 / 60}min"
    } else if (timeInMillis / 1000 >= 1) {
        "Less than a minute"
    } else {
        "0 min"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.habitList(
    habitDataList: List<HabitUiModel>,
    onCheckHabit: (Long) -> Unit,
    onOpenHabit: (Long) -> Unit
) {
    items(items = habitDataList, key = { it.habitId }) { habitUiModel ->
        HabitCard(
            habitUiModel = habitUiModel,
            onCheckHabit = onCheckHabit,
            onOpenHabitDetails = { habitId ->
                onOpenHabit(habitId)
            }
        )
    }
}

fun getApplicationLabel(packageName: String, context: Context): String {
    val appInfo =
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return context.packageManager.getApplicationLabel(appInfo).toString()
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SummaryScreenPrev() {
    SummaryScreen(
        usageStats = emptyList(),
        unlockCount = 100,
        notificationCount = 230,
        habitDataList = emptyList(),
        onCheckHabit = {},
        onOpenHabit = {}
    )
}

