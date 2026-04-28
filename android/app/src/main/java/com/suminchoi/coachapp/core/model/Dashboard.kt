package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val schedule: Schedule = Schedule(),
    @SerialName("currentPlan") val currentPlan: List<PlannedWorkout> = emptyList(),
    @SerialName("recentActivities") val recentActivities: List<Activity> = emptyList(),
)

@Serializable
data class Schedule(
    @SerialName("nextRunAt") val nextRunAt: String? = null,
    @SerialName("lastRunAt") val lastRunAt: String? = null,
    @SerialName("lastStatus") val lastStatus: String? = null,
    @SerialName("lastError") val lastError: String? = null,
    @SerialName("failureCount") val failureCount: Int = 0,
)

@Serializable
data class PlannedWorkout(
    val date: String = "",
    @SerialName("workoutName") val name: String = "",
    @SerialName("sessionType") val sessionType: String? = null,
    @SerialName("workoutType") val workoutType: String? = null,
    @SerialName("plannedMinutes") val plannedMinutes: Int? = null,
    @SerialName("isRest") val isRest: Boolean = false,
)

@Serializable
data class Activity(
    val provider: String? = null,
    @SerialName("providerActivityId") val providerActivityId: String? = null,
    @SerialName("activityDate") val activityDate: String = "",
    @SerialName("startedAt") val startedAt: String? = null,
    val title: String = "",
    @SerialName("sportType") val sportType: String? = null,
    @SerialName("distanceKm") val distanceKm: Double? = null,
    @SerialName("durationSeconds") val durationSeconds: Int? = null,
    @SerialName("avgPace") val avgPace: String? = null,
    @SerialName("avgPaceSeconds") val avgPaceSeconds: Int? = null,
    @SerialName("avgHr") val averageHr: Int? = null,
    @SerialName("executionQuality") val executionQuality: String? = null,
    @SerialName("targetMatchScore") val targetMatchScore: Double? = null,
    @SerialName("sessionType") val sessionType: String? = null,
)

enum class Zone {
    RECOVERY, BASE, THRESHOLD, INTERVAL, REST, LONG;
}

fun String.toZone(): Zone = when (this.lowercase()) {
    "recovery" -> Zone.RECOVERY
    "base" -> Zone.BASE
    "threshold" -> Zone.THRESHOLD
    "interval" -> Zone.INTERVAL
    "rest" -> Zone.REST
    "long" -> Zone.LONG
    else -> Zone.REST
}

fun Zone.label(): String = when (this) {
    Zone.RECOVERY -> "회복"
    Zone.BASE -> "기본"
    Zone.THRESHOLD -> "역치"
    Zone.INTERVAL -> "인터벌"
    Zone.REST -> "휴식"
    Zone.LONG -> "장거리"
}

fun formatPace(secondsPerKm: Int): String {
    val minutes = secondsPerKm / 60
    val seconds = secondsPerKm % 60
    return "%d:%02d/km".format(minutes, seconds)
}
