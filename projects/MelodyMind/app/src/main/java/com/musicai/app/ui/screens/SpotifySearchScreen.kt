@Composable
fun SpotifySearchScreen(
    spotifyVm: SpotifyViewModel = viewModel(),
    playerVm: PreviewPlayerViewModel = viewModel()
) {
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
