package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePreferencesRequest(
    val timezone: String? = null,
    val locale: String? = null,
    @SerialName("schedule_times") val scheduleTimes: List<String>? = null,
    @SerialName("run_mode") val runMode: String? = null,
    @SerialName("include_strength") val includeStrength: Boolean? = null,
)

@Serializable
data class FeedbackRequest(
    @SerialName("feedback_date") val feedbackDate: String,
    val fatigue: Int? = null,
    val soreness: Int? = null,
    val stress: Int? = null,
    val motivation: Int? = null,
    val sleep: Int? = null,
    @SerialName("pain_notes") val painNotes: String? = null,
    val notes: String? = null,
)

@Serializable
data class GoalRequest(
    @SerialName("goal_type") val goalType: String,
    @SerialName("target_distance_km") val targetDistanceKm: Double? = null,
    @SerialName("target_time_seconds") val targetTimeSeconds: Int? = null,
    @SerialName("target_date") val targetDate: String? = null,
    val notes: String? = null,
)

@Serializable
data class AvailabilityRequest(
    @SerialName("available_days") val availableDays: List<String>,
    @SerialName("long_run_day") val longRunDay: String? = null,
)

@Serializable
data class InjuryRequest(
    @SerialName("injury_type") val injuryType: String,
    val severity: String,
    val notes: String? = null,
)

@Serializable
data class SyncRequest(
    val days: Int = 7,
)

@Serializable
data class CreateUserRequest(
    val email: String,
    @SerialName("display_name") val displayName: String,
)

@Serializable
data class CreateUserResponse(
    val id: String,
    @SerialName("api_key") val apiKey: String,
)
