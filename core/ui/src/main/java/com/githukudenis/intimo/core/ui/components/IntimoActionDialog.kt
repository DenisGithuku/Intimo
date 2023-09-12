package com.githukudenis.intimo.core.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.githukudenis.intimo.core.ui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IntimoActionDialog(
    icon: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
    onDismissRequest: () -> Unit
) {
    val animateTrigger = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        launch {
            animateTrigger.value = true
        }
    }
    Dialog(
        onDismissRequest = {
            scope.launch {
                animateTrigger.value = false
                delay(100)
                onDismissRequest()
            }
        }
    ) {
        AnimatedScaleInTransition(visible = animateTrigger.value) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (icon != null) {
                        icon()
                    }
                    title()
                    description()
                    content()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(
                                text = stringResource(id = R.string.cancel_button_text)
                            )
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextButton(onClick = onDismissRequest) {
                            Text(
                                text = stringResource(id = R.string.accept_button_text)
                            )
                        }
                    }
                }
            }
        }
    }
}