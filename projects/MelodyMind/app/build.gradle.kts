import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" // Kotlin 2.x Compose Compiler
}

// MUST BE TOP-LEVEL (not inside android {})
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.musicai.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.musicai.app"
        minSdk = 24
        targetSdk = 36

        // Version sourced from gradle.properties
        versionCode = (project.findProperty("VERSION_CODE") as String).toInt()
        versionName = "1.0"

        // BuildConfig constants from GitHub Secrets or local.properties
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

    // Compose Compiler (Kotlin 2.x)
    composeCompiler {
        enableStrongSkippingMode = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

/* --------------------------------------------------
   CI Helper Tasks (used by GitHub Actions)
 -------------------------------------------------- */
tasks.register("printVersionName") {
    doLast { println(android.defaultConfig.versionName) }
}
tasks.register("printVersionCode") {
    doLast { println(android.defaultConfig.versionCode) }
}

/* --------------------------------------------------
   Auto-increment versionCode (CI usage)
 -------------------------------------------------- */
tasks.register("bumpVersionCode") {
    doLast {
        val gradlePropertiesFile = rootProject.file("gradle.properties")
        val props = Properties()

        FileInputStream(gradlePropertiesFile).use { props.load(it) }

        val currentCode = props.getProperty("VERSION_CODE")?.toIntOrNull() ?: 1
        val newCode = currentCode + 1

        props.setProperty("VERSION_CODE", newCode.toString())

        println("🔼 Bumping versionCode: $currentCode → $newCode")

        FileOutputStream(gradlePropertiesFile).use { props.store(it, null) }
    }
}

dependencies {
    // Compose BOM (keeps UI libs aligned)
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Media3 (audio/video engine)
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
