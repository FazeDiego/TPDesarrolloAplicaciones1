package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.safewalk.navigation.AppNavigation
import com.example.safewalk.ui.theme.SafeWalkTheme
import com.example.safewalk.viewmodel.ThemeViewModel
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Inicializar ThemeViewModel
        themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]
        themeViewModel.loadTheme(this)

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

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // 👇 CONTROLAR STATUS BAR Y NAV BAR
            androidx.compose.runtime.SideEffect {
                val window = this.window
                val insetsController =
                    androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)

                // ✔ Cambia color de íconos según el tema
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme

                // ✔ Podés tener la status bar transparente
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
            }

            SafeWalkTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    hasSeenOnboarding,
                    isLoggedIn,
                    themeViewModel
                )
            }
        }
    }
    private fun hasSeenOnboarding(): Boolean {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        return prefs.getBoolean("hasSeenOnboarding", false)
    }
}
