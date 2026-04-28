package com.suminchoi.coachapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.core.auth.AuthState
import com.suminchoi.coachapp.core.auth.AuthStore
import com.suminchoi.coachapp.design.CoachAppTheme
import com.suminchoi.coachapp.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authStore: AuthStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoachAppTheme {
                val authState by authStore.state.collectAsStateWithLifecycle()
                AppNavigation(isSignedIn = authState is AuthState.SignedIn)
            }
        }
    }
}
