package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.core.model.Zone
import com.suminchoi.coachapp.core.model.label
import com.suminchoi.coachapp.core.model.toZone
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.RcTypography

fun zoneColor(zone: Zone) = when (zone) {
    Zone.RECOVERY  -> ZoneColors.recovery
    Zone.BASE      -> ZoneColors.base
    Zone.THRESHOLD -> ZoneColors.threshold
    Zone.INTERVAL  -> ZoneColors.interval
    Zone.REST      -> ZoneColors.rest
    Zone.LONG      -> ZoneColors.long
}

@Composable
fun RcZoneBadge(sessionType: String, modifier: Modifier = Modifier) {
    val zone = sessionType.toZone()
    val color = zoneColor(zone)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.22f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = zone.label().uppercase(),
            style = RcTypography.labelSmall,
            color = color,
        )
    }
}

@Composable
fun RcZoneDot(sessionType: String, modifier: Modifier = Modifier) {
    val color = zoneColor(sessionType.toZone())
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {}
}
