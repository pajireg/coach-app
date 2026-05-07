package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.rcColors

@Composable
fun ScoreSlider(
    label: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = ZoneColors.base
    val effectiveValue = value ?: 0

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(label, style = RcTypography.bodyMedium, color = rcColors.text)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (value != null) "$value" else "−",
                    style = RcTypography.monoBody.copy(fontSize = 22.sp),
                    color = if (value != null) activeColor else rcColors.textMuted,
                )
                if (value != null) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "/10",
                        style = RcTypography.labelSmall,
                        color = rcColors.textMuted,
                        modifier = Modifier.padding(bottom = 3.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // stepped dot track
        Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
            // background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(rcColors.border)
                    .align(Alignment.Center),
            )
            // active fill
            if (effectiveValue > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = ((effectiveValue - 1).toFloat() / 9f).coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(activeColor)
                        .align(Alignment.CenterStart),
                )
            }
            // dot buttons
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (i in 1..10) {
                    val isActive = i == effectiveValue
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isActive) activeColor else rcColors.bgElev2)
                            .clickable { onValueChange(if (effectiveValue == i) null else i) },
                    )
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(rcColors.border))
}
