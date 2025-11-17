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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunityScreen(onNextClick: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Comunidad",
                        color = Color.White, // tono gris oscuro suave
                        fontSize = 30.sp,

                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, // 👈 centra el texto visualmente
                        fontFamily = Archivo
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent, // o tu color de barra
                    titleContentColor = Color.Black
                )
            )
        },
        content = { paddingValues ->
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
                    .padding(paddingValues)
            ) {
                // Contenido central
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp), // margen lateral opcional
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                        Image(
                            painter = painterResource(R.drawable.community),
                            contentDescription = "Ilustración de comunidad colaborativa",
                            modifier = Modifier.size(300.dp)
                        )



                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Compartí tu experiencia y ayuda a otros",
                        color = Color.White, // tono gris oscuro suave
                        fontSize = 25.sp,
                        lineHeight = 45.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, // 👈 centra el texto visualmente
                        fontFamily = Archivo
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Tu opinion es valiosa. Contribuye con tus conocimientos y consejos para enriquecer a nuestra comunidad y guiar a otros en su camino",
                        color = Color.White, // tono gris oscuro suave
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
    )
}

