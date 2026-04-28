package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendsResponse(
    @SerialName("asOf") val asOf: String = "",
    val acwr: Double? = null,
    @SerialName("weeklyVolume") val weeklyVolume: List<TrendWeek> = emptyList(),
    @SerialName("paceTrend") val paceTrend: List<PaceTrendPoint> = emptyList(),
)

@Serializable
data class TrendWeek(
    @SerialName("weekStart") val weekStart: String = "",
    @SerialName("distanceKm") val distanceKm: Double = 0.0,
    @SerialName("runCount") val runCount: Int = 0,
    @SerialName("longRunKm") val longRunKm: Double? = null,
    val acwr: Double? = null,
)

@Serializable
data class PaceTrendPoint(
    @SerialName("activityDate") val activityDate: String = "",
    val title: String = "",
    @SerialName("distanceKm") val distanceKm: Double? = null,
    @SerialName("avgPace") val avgPace: String? = null,
    @SerialName("avgPaceSeconds") val avgPaceSeconds: Int? = null,
)
