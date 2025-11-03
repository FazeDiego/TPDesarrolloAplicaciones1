package com.example.safewalk.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.safewalk.R

@Composable
fun OnboardingScreen3(onNextClick: () -> Unit) {
    OnboardingScreen(
        title = "Comparte tu experiencia y ayuda a otros",
        imageRes = R.drawable.logo,
        buttonText = "Siguiente",
        buttonColor = Color(0xFF9C64D6),
        onNextClick = onNextClick
    )
}