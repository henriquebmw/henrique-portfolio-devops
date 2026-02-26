package com.musicai.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.musicai.app.viewmodel.PreviewPlayerViewModel

@Composable
fun SpotifyTrackRow(
    title: String,
    artists: String,
    cover: String?,
    previewUrl: String?,
    playerVm: PreviewPlayerViewModel
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !previewUrl.isNullOrBlank()) {
                previewUrl?.let { playerVm.play(it) }
            }
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (cover != null) {
                Image(
                    painter = rememberAsyncImagePainter(cover),
                    contentDescription = "cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(artists, style = MaterialTheme.typography.bodySmall)
            }
            if (!previewUrl.isNullOrBlank()) {
                Text("▶", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
