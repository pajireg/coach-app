package com.suminchoi.coachapp.features.trends

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.rcColors

@Composable
fun TrendsScreen() {
    RcScreen(title = stringResource(R.string.tab_trends)) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.trends_coming_soon),
                    style = RcTypography.titleMedium,
                    color = rcColors.textDim,
                )
            }
        }
    }
}
