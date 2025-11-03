package com.example.safewalk.ui

import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

import com.example.safewalk.R


@Composable
fun OnboardingScreen1(onNextClick: () -> Unit) {
    OnboardingScreen(
        title = "Tu camino seguro comienza aquí",
        imageRes = R.drawable.logo,      // el logo que quieras mostrar
        buttonText = "Siguiente",
        buttonColor = Color(0xFF9C64D6),
        onNextClick = onNextClick       // pasa la lambda que navega a la siguiente pantalla
    )
}
