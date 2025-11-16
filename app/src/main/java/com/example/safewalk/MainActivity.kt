package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.safewalk.navigation.AppNavigation
import com.example.safewalk.ui.theme.SafeWalkTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Inicializamos Google Places (solo una vez)
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyA0CVUeZr8KRx_J55WDq_-hrOTXBYk93Ns"
            )
        }

        // ⚡ LEER Onboarding solo una vez (SharedPref)
        val hasSeenOnboarding = hasSeenOnboarding()

        // ⚡ Detecta si hay usuario con sesión activa en Firebase
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        // Leer preferencia de tema (claro/oscuro)
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val useDarkTheme = prefs.getBoolean("useDarkTheme", false)

        setContent {
            SafeWalkTheme(darkTheme = useDarkTheme) {
                AppNavigation(
                    hasSeenOnboarding = hasSeenOnboarding,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }

    private fun hasSeenOnboarding(): Boolean {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        return prefs.getBoolean("hasSeenOnboarding", false)
    }
}
