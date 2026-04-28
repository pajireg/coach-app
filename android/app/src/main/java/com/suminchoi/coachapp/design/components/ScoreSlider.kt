package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = RcTypography.bodyMedium, color = rcColors.text)
            Text(
                text = value?.toString() ?: "−",
                style = RcTypography.monoBody,
                color = if (value != null) ZoneColors.base else rcColors.textMuted,
            )
        }
        Slider(
            value = (value ?: 0).toFloat(),
            onValueChange = { v ->
                val intVal = v.toInt().coerceIn(0, 10)
                onValueChange(if (intVal == 0) null else intVal)
            },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = ZoneColors.base,
                activeTrackColor = ZoneColors.base,
            ),
        )
    }
}
