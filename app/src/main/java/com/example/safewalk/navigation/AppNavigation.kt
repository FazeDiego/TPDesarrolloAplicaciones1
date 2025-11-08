package com.example.safewalk.navigation

import com.example.safewalk.ui.SplashScreen



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safewalk.ui.ComunityScreen
import com.example.safewalk.ui.HomeScreen
import com.example.safewalk.ui.LoginScreen
import com.example.safewalk.ui.NewReportScreen

import com.example.safewalk.ui.OnboardingScreen1
import com.example.safewalk.ui.OnboardingScreen2
import com.example.safewalk.ui.ProfileScreen

import com.example.safewalk.ui.RegisterScreen
import com.example.safewalk.ui.ReportScreen
import com.example.safewalk.ui.ReportSegmentScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
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
                navController.navigate("comunity") {
                    popUpTo("onboarding2") { inclusive = true }
                }
            })
        }

        composable("comunity") {
            ComunityScreen(onNextClick = {
                navController.navigate("registerScreen") {
                    popUpTo("comunity") { inclusive = true }
                }
            })
        }






        composable("loginScreen") {
            LoginScreen(navController = navController)  // pasás el navController
        }

        composable("registerScreen") {
            RegisterScreen(navController = navController)  // pasás el navController
        }

        composable("reportScreen") {
            ReportScreen(navController = navController)  // pasás el navController
        }

        composable("profileScreen") {
            ProfileScreen(navController = navController)  // pasás el navController
        }

        composable("newReportScreen") {
            NewReportScreen(navController = navController)  // pasás el navController
        }

        composable("reportSegmentScreen") {
            ReportSegmentScreen(navController = navController)  // pasás el navController
        }






        composable("home") {
            HomeScreen(navController)
        }
    }
}
