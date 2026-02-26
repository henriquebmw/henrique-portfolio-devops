package com.musicai.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicai.app.viewmodel.SpotifyViewModel
import com.musicai.app.viewmodel.PreviewPlayerViewModel
import com.musicai.app.ui.components.PlayerControlBar
import com.musicai.app.ui.components.SpotifyTrackRow

// additional helpers for credential input
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SpotifySearchScreen(
    spotifyVm: SpotifyViewModel = viewModel(),
    playerVm: PreviewPlayerViewModel = viewModel()
) {
    // credential inputs (only shown if not configured)
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }

    if (!spotifyVm.hasCredentials()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Spotify credentials required", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = clientSecret,
                onValueChange = { clientSecret = it },
                label = { Text("Client Secret") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (clientId.isNotBlank() && clientSecret.isNotBlank()) {
                        spotifyVm.setSpotifyCredentials(clientId, clientSecret)
                    }
                },
                enabled = clientId.isNotBlank() && clientSecret.isNotBlank(),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save & Continue")
            }
        }
        return
    }

    var query by remember { mutableStateOf("") }
    val isPlaying by playerVm.isPlaying.collectAsState()
    val tracks by spotifyVm.tracks.collectAsState()
    val loading by spotifyVm.loading.collectAsState()
    val error by spotifyVm.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search tracks") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { if (query.isNotBlank()) spotifyVm.search(query) }
                ) { Text("Search") }

                if (loading) {
                    CircularProgressIndicator(Modifier.size(28.dp))
                }
            }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            if (tracks.isEmpty()) {
                Text("Try searching for an artist or song.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tracks) { track ->
                        SpotifyTrackRow(
                            title = track.name,
                            artists = track.artists.joinToString { it.name },
                            cover = track.album.images.firstOrNull()?.url,
                            previewUrl = track.preview_url,
                            playerVm = playerVm
                        )
                    }
                }
            }
        }

        // STICKY PLAYER BAR
        if (isPlaying) {
            PlayerControlBar(
                playerVm = playerVm,
                trackTitle = "Preview Playing..."
            )
        }
    }
}
