package com.githukudenis.intimo.feature.onboarding.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.core.ui.components.MultipleClicksCutter
import com.githukudenis.intimo.core.ui.components.get
import com.githukudenis.intimo.feature.onboarding.R

@Composable
fun GetStartedBtn(
    uiIsValid: Boolean,
    multipleClicksCutter: MultipleClicksCutter = remember { MultipleClicksCutter.get() },
    onGetStarted: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { multipleClicksCutter.processEvent(onGetStarted) },
        shape = MaterialTheme.shapes.small,
        enabled = uiIsValid,
    ) {
        Text(
            text = stringResource(R.string.get_started_btn_txt),
            modifier = Modifier.padding(6.dp)
        )
    }
}