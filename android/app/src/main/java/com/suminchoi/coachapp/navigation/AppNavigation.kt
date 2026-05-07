package com.suminchoi.coachapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.design.components.RcTab
import com.suminchoi.coachapp.design.components.RcTabBar
import com.suminchoi.coachapp.features.feedback.FeedbackSheet
import com.suminchoi.coachapp.features.goals.GoalsScreen
import com.suminchoi.coachapp.features.home.HomeScreen
import com.suminchoi.coachapp.features.onboarding.OnboardingScreen
import com.suminchoi.coachapp.features.settings.SettingsScreen
import com.suminchoi.coachapp.features.trends.TrendsScreen
import com.suminchoi.coachapp.features.weekly.WeeklyScreen
import com.suminchoi.coachapp.features.workout.WorkoutSheet

@Composable
fun AppNavigation(isSignedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isSignedIn) MainRoute else OnboardingRoute

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<OnboardingRoute> {
            OnboardingScreen(onSuccess = {
                navController.navigate(MainRoute) {
                    popUpTo<OnboardingRoute> { inclusive = true }
                }
            })
        }
        composable<MainRoute> {
            MainScreen(onSignOut = {
                navController.navigate(OnboardingRoute) {
                    popUpTo<MainRoute> { inclusive = true }
                }
            })
        }
    }
}

@Composable
private fun MainScreen(onSignOut: () -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(RcTab.HOME) }
    var showFeedback by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<PlannedWorkout?>(null) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            RcTab.HOME -> HomeScreen(
                onOpenFeedback = { showFeedback = true },
                onOpenWorkout = { workout, activity ->
                    selectedWorkout = workout
                    selectedActivity = activity
                },
            )
            RcTab.WEEKLY -> WeeklyScreen(onOpenWorkout = { workout, activity ->
                selectedWorkout = workout
                selectedActivity = activity
            })
            RcTab.TRENDS -> TrendsScreen()
            RcTab.GOALS -> GoalsScreen()
            RcTab.SETTINGS -> SettingsScreen()
        }

        RcTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
        )
    }

    if (showFeedback) {
        FeedbackSheet(onDismiss = { showFeedback = false })
    }

    if (selectedWorkout != null) {
        WorkoutSheet(
            workout = selectedWorkout,
            activity = selectedActivity,
            onDismiss = { selectedWorkout = null; selectedActivity = null },
        )
    }
}
