package com.musicai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.musicai.app.ui.navigation.RootNav
import com.musicai.app.ui.theme.MelodyMindTheme

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Enable edge-to-edge (transparent system bars)
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
    insetsController.isAppearanceLightStatusBars = false   // White text for dark backgrounds
    insetsController.isAppearanceLightNavigationBars = false

    setContent {
        MelodyMindTheme {
            RootNav()
        }
    }
}
