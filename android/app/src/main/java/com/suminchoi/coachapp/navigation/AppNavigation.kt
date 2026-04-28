package com.suminchoi.coachapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.design.components.RcTabBar
import com.suminchoi.coachapp.design.components.TabItem
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
    val tabs = listOf(
        TabItem(label = "오늘", icon = Icons.Default.Home),
        TabItem(label = "주간", icon = Icons.Default.DateRange),
        TabItem(label = "추이", icon = Icons.Default.ShowChart),
        TabItem(label = "목표", icon = Icons.Default.TrackChanges),
        TabItem(label = "설정", icon = Icons.Default.Settings),
    )

    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<PlannedWorkout?>(null) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> HomeScreen(
                onOpenFeedback = { showFeedback = true },
                onOpenWorkout = { selectedWorkout = it },
            )
            1 -> WeeklyScreen(onOpenWorkout = { selectedWorkout = it })
            2 -> TrendsScreen()
            3 -> GoalsScreen()
            4 -> SettingsScreen()
        }

        RcTabBar(
            tabs = tabs,
            selectedIndex = selectedTab,
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
