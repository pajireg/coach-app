package com.suminchoi.coachapp.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.rcColors

@Composable
fun RcNavBar(
    title: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    val colors = rcColors
    val bgColor = if (colors.isDark) Color(0xFF121A2B).copy(alpha = 0.88f)
    else Color.White.copy(alpha = 0.88f)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = RcTypography.titleMedium,
                    color = colors.text,
                )
                Spacer(modifier = Modifier.weight(1f))
                actions()
            }
        }
    }
}
