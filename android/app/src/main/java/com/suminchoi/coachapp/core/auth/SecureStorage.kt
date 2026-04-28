package com.suminchoi.coachapp.core.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "coach_secure_prefs"
private const val KEY_API_KEY = "coach.apiKey"
private const val KEY_BASE_URL = "coach.baseUrl"
private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000"

@Singleton
class SecureStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun load(): AuthState {
        val key = prefs.getString(KEY_API_KEY, null)
        val url = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        return if (key != null) AuthState.SignedIn(key, url) else AuthState.SignedOut
    }

    fun save(apiKey: String, baseUrl: String) {
        prefs.edit()
            .putString(KEY_API_KEY, apiKey)
            .putString(KEY_BASE_URL, baseUrl)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
