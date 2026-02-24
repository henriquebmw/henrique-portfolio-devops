package com.musicai.app.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.musicai.app.R

@Composable
fun MelodyBottomNav(
    selected: String,
    onSelect: (String) -> Unit
) {
    NavigationBar {

        val items = listOf(
            Dest.Chat,
            Dest.Home,
            Dest.Search,
            Dest.Analyze,
            Dest.AiAssistant
        )

        items.forEach { dest ->

            val iconRes = when (dest) {
                Dest.Home -> R.drawable.ic_home_solid
                Dest.Search -> R.drawable.ic_search_solid
                Dest.Analyze -> R.drawable.ic_analyze_solid
                Dest.AiAssistant -> R.drawable.ic_ai_solid
                Dest.Chat -> R.drawable.ic_chat_solid
            }

            NavigationBarItem(
                selected = selected == dest.route,
                onClick = { onSelect(dest.route) },
                icon = {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = dest.route
                    )
                },
                label = {
                    Text(
                        dest.route
                            .replace("_", " ")
                            .replaceFirstChar { it.uppercase() }
                    )
                }
            )
        }
    }
}
