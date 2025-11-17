package com.example.safewalk.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safewalk.ui.*
import com.example.safewalk.viewmodel.AuthViewModel
import com.example.safewalk.viewmodel.SegmentViewModel
import com.example.safewalk.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    hasSeenOnboarding: Boolean,
    isLoggedIn: Boolean,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val segmentViewModel: SegmentViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 🔥 LÓGICA REAL DE NAVEGACIÓN
    val startDestination =
        when {
            !hasSeenOnboarding -> "splash"       // aún no vio onboarding
            isLoggedIn -> "home"                 // sesión iniciada → directo al home
            else -> "loginScreen"                // vio onboarding pero no está logueado
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("splash") {
            SplashScreen(onStartClick = {
                navController.navigate("onboarding1") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("onboarding1") {
            OnboardingScreen1(onNextClick = {
                navController.navigate("onboarding2") {
                    popUpTo("onboarding1") { inclusive = true }
                }
            })
        }

        composable("onboarding2") {
            OnboardingScreen2(onNextClick = {

                // Guardar en SharedPreferences
                val prefs = navController.context.getSharedPreferences("my_prefs", 0)
                prefs.edit().putBoolean("hasSeenOnboarding", true).apply()

                navController.navigate("comunity") {
                    popUpTo("onboarding2") { inclusive = true }
                }
            })
        }

        composable("comunity") {
            ComunityScreen(onNextClick = { navController.navigate("registerScreen") })
        }

        composable("loginScreen") {
            LoginScreen(navController)
        }

        composable("registerScreen") {
            RegisterScreen(navController, authViewModel)
        }

        composable("reportScreen") {
            ReportScreen(navController, segmentViewModel)
        }

        composable("profileScreen") {
            ProfileScreen(navController)
        }

        composable("newReportScreen") {
            NewReportScreen(navController, segmentViewModel)
        }

        composable("reportSegmentScreen") {
            ReportSegmentScreen(navController)
        }

        composable("reportsScreen") {
            ReportsScreen(navController)
        }

        composable("home") {
            HomeScreen(navController, themeViewModel)
        }
    }
}
