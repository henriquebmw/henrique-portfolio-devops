package com.musicai.app

import android.app.Application
import android.util.Log

/**
 * Global application class for MelodyMind.
 *
 * Used to initialize:
 * - Analytics / logs
 * - Retrofit singletons
 * - AI engines
 * - Audio analyzer global state (if needed)
 * - Dependency injection (if added later)
 *
 * Safe, lightweight, and fully compatible with AGP 9 + Kotlin 2.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Basic startup log
        Log.i("MelodyMind", "App started successfully (Application.onCreate)")

        // Example: preload OpenAI/Spotify services if desired
        // OpenAIService.init(this)
        // SpotifyApi.init(this)

        // Example: initialize global crash logging
        // CrashReporting.init(this)

        // Example: initialize DI container (Koin / Hilt not required)
        // DependencyContainer.init(this)
    }
}
