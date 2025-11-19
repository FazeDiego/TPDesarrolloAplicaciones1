package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.WindowCompat
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

        themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]
        themeViewModel.loadTheme(this)

        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyA0CVUeZr8KRx_J55WDq_-hrOTXBYk93Ns"
            )
        }

        val hasSeenOnboarding = hasSeenOnboarding()
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        // ⬅️ SOLUCIÓN PRINCIPAL
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            androidx.compose.runtime.SideEffect {
                val insetsController =
                    WindowCompat.getInsetsController(window, window.decorView)

                // Cambia color de íconos según tema
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme

                // Status/NAV bar transparentes sin generar fondos grises
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
