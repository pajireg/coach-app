package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.Radius
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.rcColors

enum class RcButtonStyle { PRIMARY, SECONDARY, GHOST }

@Composable
fun RcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: RcButtonStyle = RcButtonStyle.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(Radius.pill)
    val buttonModifier = modifier.height(52.dp)

    when (style) {
        RcButtonStyle.PRIMARY -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = ZoneColors.base),
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            else Text(text, style = RcTypography.titleMedium, color = Color.White)
        }

        RcButtonStyle.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = rcColors.text),
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text(text, style = RcTypography.titleMedium)
        }

        RcButtonStyle.GHOST -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !isLoading,
        ) {
            Text(text, style = RcTypography.bodyMedium, color = rcColors.textDim)
        }
    }
}
