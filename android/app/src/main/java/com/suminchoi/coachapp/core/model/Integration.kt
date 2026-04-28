package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationsResponse(
    val integrations: List<Integration> = emptyList(),
)

@Serializable
data class Integration(
    val provider: String = "",
    @SerialName("displayName") val displayName: String = "",
    val status: String = "not_connected",
    val connected: Boolean = false,
    val source: String = "none",
    val capabilities: List<String> = emptyList(),
    @SerialName("externalAccountId") val externalAccountId: String? = null,
    @SerialName("lastError") val lastError: String? = null,
)
