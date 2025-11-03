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
import com.example.safewalk.ui.HomeScreen
import com.example.safewalk.ui.LoginScreen

import com.example.safewalk.ui.OnboardingScreen1
import com.example.safewalk.ui.OnboardingScreen2
import com.example.safewalk.ui.OnboardingScreen3
import com.example.safewalk.ui.OnboardingScreen4
import com.example.safewalk.ui.RegisterScreen

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
                navController.navigate("onboarding3") {
                    popUpTo("onboarding2") { inclusive = true }
                }
            })
        }

        composable("onboarding3") {
            OnboardingScreen3(onNextClick = {
                navController.navigate("onboarding4") {
                    popUpTo("onboarding3") { inclusive = true }
                }
            })
        }


        composable("onboarding4") {
            OnboardingScreen4(onNextClick = {
                navController.navigate("loginScreen") {
                    popUpTo("onboarding4") { inclusive = true }
                }
            })
        }

        composable("loginScreen") {
            LoginScreen(navController = navController)  // pasás el navController
        }

        composable("registerScreen") {
            RegisterScreen(navController = navController)  // pasás el navController
        }




        composable("home") {
            HomeScreen()
        }
    }
}
