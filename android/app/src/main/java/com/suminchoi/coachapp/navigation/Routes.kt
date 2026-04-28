package com.suminchoi.coachapp.navigation

import kotlinx.serialization.Serializable

@Serializable object OnboardingRoute
@Serializable object MainRoute

@Serializable object HomeRoute
@Serializable object WeeklyRoute
@Serializable object TrendsRoute
@Serializable object GoalsRoute
@Serializable object SettingsRoute

@Serializable data class WorkoutDetailRoute(val workoutId: String)
