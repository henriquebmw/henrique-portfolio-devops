package com.musicai.app.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple wrapper around SharedPreferences for storing API credentials.
 * Values default to empty string when not set.
 */
class CredentialsStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("melodymind_creds", Context.MODE_PRIVATE)

    var spotifyClientId: String
        get() = prefs.getString("spotify_client_id", "") ?: ""
        set(value) = prefs.edit().putString("spotify_client_id", value).apply()

    var spotifyClientSecret: String
        get() = prefs.getString("spotify_client_secret", "") ?: ""
        set(value) = prefs.edit().putString("spotify_client_secret", value).apply()

    var openAiKey: String
        get() = prefs.getString("openai_api_key", "") ?: ""
        set(value) = prefs.edit().putString("openai_api_key", value).apply()
}
