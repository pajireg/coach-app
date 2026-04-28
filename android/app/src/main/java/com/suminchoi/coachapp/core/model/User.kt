package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    @SerialName("display_name") val displayName: String = "",
    @SerialName("garmin_email") val garminEmail: String? = null,
    val preferences: Preferences = Preferences(),
    @SerialName("integration_status") val integrationStatus: IntegrationStatus = IntegrationStatus(),
)

@Serializable
data class Preferences(
    val timezone: String = "Asia/Seoul",
    val locale: String = "ko",
    @SerialName("schedule_times") val scheduleTimes: List<String> = emptyList(),
    @SerialName("run_mode") val runMode: String = "base",
    @SerialName("include_strength") val includeStrength: Boolean = false,
)

@Serializable
data class IntegrationStatus(
    val garmin: String = "not_connected",
    val strava: String = "not_connected",
)
