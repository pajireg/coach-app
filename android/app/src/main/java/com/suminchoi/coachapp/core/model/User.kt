package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("userId") val id: String = "",
    @SerialName("externalKey") val externalKey: String = "",
    val email: String = "",
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("garminEmail") val garminEmail: String? = null,
    val preferences: Preferences = Preferences(),
    @SerialName("integrationStatus") val integrationStatus: IntegrationStatus = IntegrationStatus(),
)

@Serializable
data class Preferences(
    val timezone: String = "Asia/Seoul",
    val locale: String = "ko",
    @SerialName("scheduleTimes") val scheduleTimes: String = "",
    @SerialName("runMode") val runMode: String = "base",
    @SerialName("includeStrength") val includeStrength: Boolean = false,
)

@Serializable
data class IntegrationStatus(
    val garmin: String = "not_connected",
    @SerialName("googleCalendar") val googleCalendar: String = "not_connected",
)
