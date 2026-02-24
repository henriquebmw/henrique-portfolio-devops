package com.musicai.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Analytics
import androidx.compose.material3.icons.filled.AutoAwesome
import androidx.compose.material3.icons.filled.GraphicEq
import androidx.compose.material3.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musicai.app.ui.navigation.Dest

@Composable
fun MainScreen(
    onNavigate: (String) -> Unit
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1DA1F2), Color(0xFF6C5CE7))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "MelodyMind",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Text(
                text = "Music • AI • Creativity",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(0.8f)
            )

            Spacer(Modifier.height(24.dp))

            FeatureButton(
                label = "Spotify Search",
                icon = Icons.Default.Search,
                onClick = { onNavigate(Dest.Search.route) }
            )

            FeatureButton(
                label = "Analyze Preview",
                icon = Icons.Default.Analytics,
                onClick = { onNavigate(Dest.Analyze.route) }
            )

            FeatureButton(
                label = "AI Assistant",
                icon = Icons.Default.AutoAwesome,
                onClick = { onNavigate(Dest.AiAssistant.route) }
            )

            FeatureButton(
                label = "AI Chat",
                icon = Icons.Default.GraphicEq,
                onClick = { onNavigate(Dest.Chat.route) }
            )
        }
    }
}

@Composable
private fun FeatureButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
