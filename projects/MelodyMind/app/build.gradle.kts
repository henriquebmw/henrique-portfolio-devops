plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") // Kotlin 2.x Compose Compiler
}

android {
    namespace = "com.musicai.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.musicai.app"
        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_ID",
            "\"${project.findProperty("SPOTIFY_CLIENT_ID") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_SECRET",
            "\"${project.findProperty("SPOTIFY_CLIENT_SECRET") ?: ""}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Compose Compiler (recommended for Kotlin 2.x)
    // Do NOT use composeOptions {} with Kotlin 2.x
    composeCompiler {
        enableStrongSkippingMode = true
    }

    // Kotlin JVM Toolchain (AGP 9 recommended)
    kotlin {
        jvmToolchain(17)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

/* --------------------------------------------------
   CI Helper Tasks — used by GitHub Actions workflow
   -------------------------------------------------- */
tasks.register("printVersionName") {
    doLast { println(android.defaultConfig.versionName) }
}

tasks.register("printVersionCode") {
    doLast { println(android.defaultConfig.versionCode) }
}

dependencies {

    // Compose BOM — stays in sync with Compose Compiler automatically
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Media3 (ExoPlayer + UI)
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")

    // Networking stack
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Images + Coroutines + Architecture
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
