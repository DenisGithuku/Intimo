package com.githukudenis.summary.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.HabitData
import com.githukudenis.summary.ui.components.CardInfo
import com.githukudenis.summary.ui.components.HabitCard
import com.githukudenis.summary.util.hasUsageAccessPermissions


@Composable
internal fun SummaryRoute(
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    onOpenHabitDetails: (Int) -> Unit
) {

    val context = LocalContext.current

    var shouldShowPermissionDialog by rememberSaveable {
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

    val permissionsAllowed = context.hasUsageAccessPermissions()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, permissionsAllowed) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    shouldShowPermissionDialog = !context.hasUsageAccessPermissions()
                }

                else -> Unit
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    if (shouldShowPermissionDialog) {
        AlertDialog(
            title = {
                Text(
                    text = context.getString(R.string.permission_dialog_title)
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
                        shouldShowPermissionDialog = false
                    }
                ) {
                    Text(
                        text = context.getString(R.string.permission_dialog_negative_button)
                    )
                }
            },
            text = {
                Text(
                    text = context.getString(R.string.permission_dialog_description)
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = LocalTonalElevation.current.large,
            onDismissRequest = {
                shouldShowPermissionDialog = false
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

@Composable
internal fun SummaryScreen(
    usageStats: List<ApplicationInfoData>,
    habitDataList: List<HabitData>,
    unlockCount: Int,
    notificationCount: Long,
    onCheckHabit: (Int) -> Unit,
    onOpenHabit: (Int) -> Unit
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    text = "Your habits"
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
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Digital wellbeing",
                style = MaterialTheme.typography.headlineSmall
            )
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
                                durationMillis = 1000,
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

                    Canvas(
                        modifier = Modifier.size(120.dp)
                    ) {
                        var startAngle = -90f

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
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        usageWithColors.forEach { appUsage ->
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
                                    text = usageStats
                                        .find { it.usageDuration.toFloat() == appUsage.first }?.packageName?.let {
                                            getApplicationLabel(
                                                it,
                                                context
                                            )
                                        } ?: "Other",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color = Color.Black.copy(alpha = 0.06f))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
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


@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.habitList(
    habitDataList: List<HabitData>,
    onCheckHabit: (Int) -> Unit,
    onOpenHabit: (Int) -> Unit
) {
    item {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            habitDataList.forEachIndexed { index, habitData ->
                HabitCard(
                    modifier = Modifier.weight(1f),
                    habitData = habitData,
                    checked = habitData.habitPoints > 0,
                    onCheckHabit = onCheckHabit,
                    onOpenHabitDetails = { habitId ->
                        onOpenHabit(habitId)
                    }
                )
            }
        }
    }
}

fun getApplicationLabel(packageName: String, context: Context): String {
    val appInfo =
        context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return context.packageManager.getApplicationLabel(appInfo).toString()
}


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

