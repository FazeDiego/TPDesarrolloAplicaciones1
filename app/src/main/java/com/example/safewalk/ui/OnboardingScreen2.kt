package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

import com.example.safewalk.R


@Composable
fun OnboardingScreen2(onNextClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color("#3AA0FF".toColorInt()),
                        Color("#0065C2".toColorInt())
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Contenido central
        // Contenido central
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp), // margen lateral opcional
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.White,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color("#0065C2".toColorInt())
                    )
                }
            }



            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Evita Calles oscuras o peligrosas",
                color = Color.White, // tono gris oscuro suave
                fontSize = 36.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // 👈 centra el texto visualmente
                fontFamily = Archivo
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Aprende a identificar y evitar rutas inseguras para llegar a tu destino con tranquilidad",
                color = Color("#b9dbfa".toColorInt()),
                fontSize = 20.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center, // 👈 centra el texto visualmente
                fontFamily = Inter
            )
        }

        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Continuar",
                tint = Color.White
            )
        }
    }
}
