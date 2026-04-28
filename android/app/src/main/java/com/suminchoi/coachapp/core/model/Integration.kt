package com.suminchoi.coachapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationsResponse(
    val integrations: List<Integration> = emptyList(),
)

@Serializable
data class Integration(
    val id: String = "",
    val provider: String = "",
    val status: String = "not_connected",
    @SerialName("last_sync") val lastSync: String? = null,
    @SerialName("account_email") val accountEmail: String? = null,
)
