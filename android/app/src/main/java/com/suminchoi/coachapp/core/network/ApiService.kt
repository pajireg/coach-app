package com.suminchoi.coachapp.core.network

import com.suminchoi.coachapp.core.model.AvailabilityRequest
import com.suminchoi.coachapp.core.model.CreateUserRequest
import com.suminchoi.coachapp.core.model.CreateUserResponse
import com.suminchoi.coachapp.core.model.DashboardResponse
import com.suminchoi.coachapp.core.model.FeedbackRequest
import com.suminchoi.coachapp.core.model.GarminCredentialRequest
import com.suminchoi.coachapp.core.model.GoalRequest
import com.suminchoi.coachapp.core.model.InjuryRequest
import com.suminchoi.coachapp.core.model.IntegrationsResponse
import com.suminchoi.coachapp.core.model.SyncRequest
import com.suminchoi.coachapp.core.model.TrendsResponse
import com.suminchoi.coachapp.core.model.UpdatePreferencesRequest
import com.suminchoi.coachapp.core.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("v1/me")
    suspend fun getMe(): User

    @GET("v1/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String,
        @Header("X-Coach-Base-Url") baseUrl: String,
    ): User

    @GET("v1/me/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("v1/me/trends")
    suspend fun getTrends(): TrendsResponse

    @GET("v1/me/integrations")
    suspend fun getIntegrations(): IntegrationsResponse

    @PUT("v1/me/integrations/garmin")
    suspend fun connectGarmin(@Body req: GarminCredentialRequest): IntegrationsResponse

    @DELETE("v1/me/integrations/garmin")
    suspend fun disconnectGarmin(): IntegrationsResponse

    @PATCH("v1/me/preferences")
    suspend fun updatePreferences(@Body req: UpdatePreferencesRequest): User

    @POST("v1/me/feedback")
    suspend fun submitFeedback(@Body req: FeedbackRequest)

    @POST("v1/me/goals")
    suspend fun submitGoal(@Body req: GoalRequest)

    @POST("v1/me/availability")
    suspend fun submitAvailability(@Body req: AvailabilityRequest)

    @POST("v1/me/injuries")
    suspend fun submitInjury(@Body req: InjuryRequest)

    @POST("v1/runs/sync")
    suspend fun triggerSync(@Body req: SyncRequest)

    @POST("v1/users")
    suspend fun createUser(@Body req: CreateUserRequest): CreateUserResponse
}
