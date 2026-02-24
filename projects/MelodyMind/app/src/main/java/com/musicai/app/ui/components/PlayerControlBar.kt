package com.musicai.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicai.app.viewmodel.PreviewPlayerViewModel

@Composable
fun PlayerControlBar(
    playerVm: PreviewPlayerViewModel,
    trackTitle: String = "Preview"
) {
    val isPlaying by playerVm.isPlaying.collectAsState()

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = trackTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            if (isPlaying) {
                Button(
                    onClick = { playerVm.pause() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Pause")
                }
            } else {
                Button(
                    onClick = { /* play handled in SearchScreen */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Play")
                }
            }
        }
    }
}
