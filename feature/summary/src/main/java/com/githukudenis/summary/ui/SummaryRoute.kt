package com.githukudenis.summary.ui

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.designsystem.theme.LocalTonalElevation
import com.githukudenis.intimo.feature.summary.R
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.summary.util.hasUsageAccessPermissions
import com.google.accompanist.drawablepainter.rememberDrawablePainter


@Composable
internal fun SummaryRoute(
    summaryViewModel: SummaryViewModel = hiltViewModel()
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
    when (val currentState = uiState) {
        SummaryUiState.Loading -> {
            Log.d("summary", "Loading")
        }

        is SummaryUiState.Success -> {
            SummaryScreen(usageStats = currentState.summaryData.usageStats.appUsageList)
        }

        is SummaryUiState.Error -> {
            Log.d("summary", "Error")
        }
    }

}

@Composable
internal fun SummaryScreen(
    usageStats: List<ApplicationInfoData>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(items = usageStats) { appInfoData ->
                ApplicationComponent(applicationInfoData = appInfoData)
            }
        }
    }
}

@Composable
fun ApplicationComponent(applicationInfoData: ApplicationInfoData) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberDrawablePainter(
                applicationInfoData.icon
            ), contentDescription = "App icon",
            modifier = Modifier.size(50.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = applicationInfoData.packageName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = applicationInfoData.usageDuration.toString(),
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = applicationInfoData.usagePercentage.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }
}