package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.core.graphics.toColorInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.safewalk.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),

)
@Composable
fun SplashScreen(onStartClick: () -> Unit) {

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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color("#0065C2".toColorInt())
                    )
                }
            }




            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tu seguridad es nuestra prioridad",
                color = Color.White,// tono gris oscuro suave
                fontSize = 36.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // 👈 centra el texto visualmente
                fontFamily = Archivo
            )




        }


        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 10.dp)
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


