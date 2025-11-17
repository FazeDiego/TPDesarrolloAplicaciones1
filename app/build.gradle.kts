plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")



}

android {
    namespace = "com.example.safewalk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.safewalk"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android / Kotlin
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-graphics:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.ui:ui-text:1.9.4")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Navigation & ViewModel
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.0")

    // OSMdroid
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation(libs.play.services.location)
    implementation(libs.androidx.compose.foundation)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.3")

    implementation("com.google.maps.android:maps-compose:2.13.0")
    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.4.0")



}

apply(plugin = "com.google.gms.google-services")

