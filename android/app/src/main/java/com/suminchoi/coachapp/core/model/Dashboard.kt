package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val schedule: Schedule = Schedule(),
    @SerialName("current_plan") val currentPlan: List<PlannedWorkout> = emptyList(),
    @SerialName("recent_activities") val recentActivities: List<Activity> = emptyList(),
)

@Serializable
data class Schedule(
    @SerialName("week_start") val weekStart: String = "",
    @SerialName("week_end") val weekEnd: String = "",
    @SerialName("total_planned_km") val totalPlannedKm: Double = 0.0,
    @SerialName("total_completed_km") val totalCompletedKm: Double = 0.0,
)

@Serializable
data class PlannedWorkout(
    val id: String = "",
    val date: String = "",
    val name: String = "",
    @SerialName("session_type") val sessionType: String = "base",
    @SerialName("planned_minutes") val plannedMinutes: Int = 0,
    @SerialName("planned_distance_km") val plannedDistanceKm: Double? = null,
    @SerialName("target_pace_per_km") val targetPacePerKm: Int? = null,
    @SerialName("is_rest") val isRest: Boolean = false,
    val notes: String? = null,
)

@Serializable
data class Activity(
    val id: String = "",
    @SerialName("garmin_activity_id") val garminActivityId: String? = null,
    val name: String = "",
    @SerialName("start_time") val startTime: String = "",
    @SerialName("duration_seconds") val durationSeconds: Int = 0,
    @SerialName("distance_km") val distanceKm: Double = 0.0,
    @SerialName("average_pace_per_km") val averagePacePerKm: Int? = null,
    @SerialName("average_hr") val averageHr: Int? = null,
    @SerialName("execution_quality") val executionQuality: Int? = null,
    @SerialName("target_match_score") val targetMatchScore: Int? = null,
    @SerialName("session_type") val sessionType: String = "base",
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
