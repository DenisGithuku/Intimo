package com.githukudenis.onboarding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.intimo.feature.onboarding.R
import com.githukudenis.onboarding.ui.PageInfo

@Composable
fun PageContent(
    pageInfo: PageInfo,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = pageInfo.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = pageInfo.image),
            modifier = Modifier
                .size(250.dp),
            contentScale = ContentScale.Crop,
            contentDescription = pageInfo.imageDescription
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pageInfo.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PageInfoPrev() {
    PageContent(pageInfo = PageInfo(
        image = R.drawable.journaling,
        imageDescription = "Example Page description",
        title = "Example page title",
        description = "Example page description"
    ))
}