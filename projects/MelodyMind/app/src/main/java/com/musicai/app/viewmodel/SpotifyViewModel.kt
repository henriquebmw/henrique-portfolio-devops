package com.musicai.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicai.app.spotify.SpotifyApi
import com.musicai.app.spotify.SpotifyTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simple ViewModel that wraps SpotifyApi search functionality and
 * exposes tracks/loading/error state for the UI.
 */
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.musicai.app.auth.CredentialsStore
import com.musicai.app.BuildConfig

class SpotifyViewModel(application: Application) : AndroidViewModel(application) {

    // credentials store (shared prefs)
    private val creds = CredentialsStore(application)

    // API instance; re-created whenever credentials change
    private var api: SpotifyApi

    init {
        val id = creds.spotifyClientId.ifBlank { BuildConfig.SPOTIFY_CLIENT_ID }
        val secret = creds.spotifyClientSecret.ifBlank { BuildConfig.SPOTIFY_CLIENT_SECRET }
        api = SpotifyApi(clientId = id, clientSecret = secret)
    }

    /**
     * Replace stored Spotify credentials and update API client.
     */
    fun setSpotifyCredentials(id: String, secret: String) {
        creds.spotifyClientId = id
        creds.spotifyClientSecret = secret
        api = SpotifyApi(clientId = id, clientSecret = secret)
    }

    fun hasCredentials(): Boolean {
        val stored = creds.spotifyClientId.isNotBlank() && creds.spotifyClientSecret.isNotBlank()
        val build = BuildConfig.SPOTIFY_CLIENT_ID.isNotBlank() && BuildConfig.SPOTIFY_CLIENT_SECRET.isNotBlank()
        return stored || build
    }

    private val _tracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun search(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val results = api.search(query)
                _tracks.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "unknown"
            } finally {
                _loading.value = false
            }
        }
    }
}
