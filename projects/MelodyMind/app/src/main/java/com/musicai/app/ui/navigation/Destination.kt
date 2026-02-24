package com.musicai.app.ui.navigation

/**
 * Navigation destinations for the MelodyMind app.
 * Each object in this sealed class represents a single screen route.
 */
sealed class Dest(val route: String) {

    object Home : Dest("home")

    object Search : Dest("search")

    object Analyze : Dest("analyze")

    object AiAssistant : Dest("ai_assistant")

    // NEW — Chat-style AI page
    object Chat : Dest("chat")
}
