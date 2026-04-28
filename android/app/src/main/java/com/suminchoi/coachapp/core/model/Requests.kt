package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePreferencesRequest(
    val timezone: String? = null,
    val locale: String? = null,
    @SerialName("scheduleTimes") val scheduleTimes: String? = null,
    @SerialName("runMode") val runMode: String? = null,
    @SerialName("includeStrength") val includeStrength: Boolean? = null,
)

@Serializable
data class FeedbackRequest(
    @SerialName("feedbackDate") val feedbackDate: String,
    @SerialName("fatigueScore") val fatigue: Int? = null,
    @SerialName("sorenessScore") val soreness: Int? = null,
    @SerialName("stressScore") val stress: Int? = null,
    @SerialName("motivationScore") val motivation: Int? = null,
    @SerialName("sleepQualityScore") val sleep: Int? = null,
    @SerialName("painNotes") val painNotes: String? = null,
    val notes: String? = null,
)

@Serializable
data class GoalRequest(
    @SerialName("goalName") val goalName: String,
    @SerialName("raceDate") val raceDate: String? = null,
    val distance: String? = null,
    @SerialName("goalTime") val goalTime: String? = null,
    @SerialName("targetPace") val targetPace: String? = null,
    val priority: Int = 1,
    @SerialName("isActive") val isActive: Boolean = true,
)

@Serializable
data class AvailabilityRequest(
    val weekday: Int,
    @SerialName("isAvailable") val isAvailable: Boolean = true,
    @SerialName("maxDurationMinutes") val maxDurationMinutes: Int? = null,
    @SerialName("preferredSessionType") val preferredSessionType: String? = null,
)

@Serializable
data class InjuryRequest(
    @SerialName("statusDate") val statusDate: String,
    @SerialName("injuryArea") val injuryArea: String,
    val severity: Int,
    val notes: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
)

@Serializable
data class SyncRequest(
    val mode: String = "auto",
)

@Serializable
data class GarminCredentialRequest(
    val email: String,
    val password: String,
)

@Serializable
data class CreateUserRequest(
    @SerialName("externalKey") val email: String,
    @SerialName("displayName") val displayName: String,
)

@Serializable
data class CreateUserResponse(
    val user: User,
    @SerialName("apiKey") val apiKey: String,
)
